package _map

import (
	"atlas-mis/point"
	"atlas-mis/wz"
	"atlas-mis/xml"
	"errors"
	"fmt"
	"math"
	"strconv"
	"strings"
	"sync/atomic"
)

func Read(mapId uint32) (*Model, error) {
	i, err := findMap(mapId)
	if err != nil {
		return nil, err
	}

	exml, err := xml.Read(i.Path())
	if err != nil {
		return nil, err
	}
	return getMapFromXML(mapId, exml)
}

func findMap(mapId uint32) (*wz.FileEntry, error) {
	paddedId := fmt.Sprintf("%09d", mapId)
	mapName := fmt.Sprintf("%s.img.xml", paddedId)
	if val, ok := wz.GetFileCache().GetFile(mapName); ok == nil {
		return val, nil
	}
	return nil, errors.New(fmt.Sprintf("map %d not found", mapId))
}

func readMapNameData(mapId uint32) (*xml.Node, error) {
	i, err := findMapName()
	if err != nil {
		return nil, err
	}
	exml, err := xml.Read(i.Path())
	if err != nil {
		return nil, err
	}
	return exml.ChildByName(getMapStringName(mapId))
}

func readNPCNameData() (*xml.Node, error) {
	i, err := findNPCName()
	if err != nil {
		return nil, err
	}
	return xml.Read(i.Path())
}

func getMapStringName(mapId uint32) string {
	b := strings.Builder{}

	if mapId < 100000000 {
		b.WriteString("maple")
	} else if mapId < 200000000 {
		b.WriteString("victoria")
	} else if mapId < 300000000 {
		b.WriteString("ossyria")
	} else if mapId < 400000000 {
		b.WriteString("elin")
	} else if mapId >= 540000000 && mapId < 560000000 {
		b.WriteString("singapore")
	} else if mapId >= 600000000 && mapId < 620000000 {
		b.WriteString("MasteriaGL")
	} else if mapId >= 677000000 && mapId < 677100000 {
		b.WriteString("Episode1GL")
	} else if mapId >= 670000000 && mapId < 682000000 {
		if (mapId >= 674030000 && mapId < 674040000) || (mapId >= 680100000 && mapId < 680200000) {
			b.WriteString("etc")
		} else {
			b.WriteString("weddingGL")
		}
	} else if mapId >= 682000000 && mapId < 683000000 {
		b.WriteString("HalloweenGL")
	} else if mapId >= 683000000 && mapId < 684000000 {
		b.WriteString("event")
	} else if mapId >= 800000000 && mapId < 900000000 {
		if mapId >= 889100000 && mapId < 889200000 {
			b.WriteString("etc")
		} else {
			b.WriteString("jp")
		}
	} else {
		b.WriteString("etc")
	}
	b.WriteString("/")
	b.WriteString(strconv.Itoa(int(mapId)))
	return b.String()
}

func findMapName() (*wz.FileEntry, error) {
	if val, ok := wz.GetFileCache().GetFile("Map.img.xml"); ok == nil {
		return val, nil
	}
	return nil, errors.New("file not found")
}

func findNPCName() (*wz.FileEntry, error) {
	if val, ok := wz.GetFileCache().GetFile("Npc.img.xml"); ok == nil {
		return val, nil
	}
	return nil, errors.New("file not found")
}

func getMapFromXML(mapId uint32, exml *xml.Node) (*Model, error) {
	i, err := exml.ChildByName("info")
	if err != nil {
		return nil, err
	}

	link := i.GetString("link", "")
	if link != "" {
		linkedMapId, err := strconv.Atoi(link)
		if err != nil {
			return nil, err
		}
		return Read(uint32(linkedMapId))
	}

	m := &Model{id: mapId}
	m.returnMapId = uint32(i.GetIntegerWithDefault("returnMap", 0))
	m.monsterRate = i.GetFloatWithDefault("mobRate", 0)

	firstUserEnter := i.GetString("onFirstUserEnter", strconv.Itoa(int(mapId)))
	if firstUserEnter == "" {
		firstUserEnter = strconv.Itoa(int(mapId))
	}
	m.onFirstUserEnter = firstUserEnter

	onUserEnter := i.GetString("onUserEnter", strconv.Itoa(int(mapId)))
	if onUserEnter == "" {
		onUserEnter = strconv.Itoa(int(mapId))
	}
	m.onUserEnter = onUserEnter

	m.fieldLimit = uint32(i.GetIntegerWithDefault("fieldLimit", 0))
	m.mobInterval = uint32(i.GetIntegerWithDefault("createMobInterval", 5000))
	m.portals = getPortals(exml)
	m.timeMob = getTimeMob(i)
	m.mapArea = getMapArea(exml, i)
	m.footholdTree = getFootholdTree(exml)
	m.areas = getAreas(exml)
	m.seats = getSeats(exml)
	m.name = getPlaceName(mapId)
	m.streetName = getStreetName(mapId)
	m.clock = getClock(exml)
	m.everLast = i.GetIntegerWithDefault("everlast", 0) > 0
	m.town = i.GetIntegerWithDefault("town", 0) > 0
	m.decHp = uint32(i.GetIntegerWithDefault("decHP", 0))
	m.protectItem = uint32(i.GetIntegerWithDefault("protectItem", 0))
	m.forcedReturnMapId = uint32(i.GetIntegerWithDefault("forcedReturn", 999999999))
	m.boat = isBoat(exml)
	m.timeLimit = i.GetIntegerWithDefault("timeLimit", -1)
	m.fieldType = uint32(i.GetIntegerWithDefault("fieldType", 0))
	m.mobCapacity = uint32(i.GetIntegerWithDefault("fixedMobCapacity", 500))
	m.recovery = i.GetFloatWithDefault("recovery", 1)
	m.backgroundTypes = getBackgroundTypes(exml)
	m.reactors = getReactors(exml)
	monsters, npcs := getLife(exml)
	m.monsters = monsters
	m.npcs = npcs
	//TODO player NPCS and CPQ support

	lp := point.NewModel(m.mapArea.x, m.mapArea.y)
	rp := point.NewModel(m.mapArea.x+m.mapArea.width, m.mapArea.y)
	fallback := point.NewModel(m.mapArea.x+int16(math.Floor(float64(m.mapArea.width/2))), m.mapArea.y)

	lp = bSearchDropPos(m.footholdTree, lp, fallback)
	rp = bSearchDropPos(m.footholdTree, rp, fallback)
	m.xLimit = XLimit{
		min: uint32(lp.X() + 14),
		max: uint32(rp.Y() - 14),
	}
	return m, nil
}

func getLife(exml *xml.Node) ([]Monster, []NPC) {
	monsters := make([]Monster, 0)
	npcs := make([]NPC, 0)
	ld, err := exml.ChildByName("life")
	if err != nil {
		return monsters, npcs
	}

	for i, life := range ld.ChildNodes {
		idstr := life.GetString("id", "")
		id, err := strconv.Atoi(idstr)
		if err != nil {
			continue
		}
		lifeType := life.GetString("type", "")
		team := life.GetIntegerWithDefault("team", -1)
		cy := int16(life.GetIntegerWithDefault("cy", 0))
		f := uint32(life.GetIntegerWithDefault("f", 0))
		fh := uint16(life.GetIntegerWithDefault("fh", 0))
		rx0 := int16(life.GetIntegerWithDefault("rx0", 0))
		rx1 := int16(life.GetIntegerWithDefault("rx1", 0))
		x := int16(life.GetIntegerWithDefault("x", 0))
		y := int16(life.GetIntegerWithDefault("y", 0))
		hide := life.GetIntegerWithDefault("hide", 0)
		mobTime := uint32(life.GetIntegerWithDefault("mobTime", 0))

		if lifeType == "m" {
			monster := Monster{
				objectId: uint32(i + 1),
				id:       uint32(id),
				mobTime:  mobTime,
				team:     team,
				cy:       cy,
				f:        f,
				fh:       fh,
				rx0:      rx0,
				rx1:      rx1,
				x:        x,
				y:        y,
				hide:     hide == 1,
			}
			monsters = append(monsters, monster)
		} else if lifeType == "n" {
			nnd, err := readNPCNameData()
			if err != nil {
				continue
			}
			nd, err := nnd.ChildByName(strconv.Itoa(id))
			if err != nil {
				continue
			}
			npc := NPC{
				objectId: uint32(i + 1),
				id:       uint32(id),
				name:     nd.GetString("name", "MISSINGNO"),
				cy:       cy,
				f:        f,
				fh:       fh,
				rx0:      rx0,
				rx1:      rx1,
				x:        x,
				y:        y,
				hide:     hide == 1,
			}
			npcs = append(npcs, npc)
		}

	}

	return monsters, npcs
}

func getReactors(exml *xml.Node) []Reactor {
	results := make([]Reactor, 0)
	rd, err := exml.ChildByName("reactor")
	if err != nil {
		return results
	}
	for _, r := range rd.ChildNodes {
		id := r.GetString("id", "")
		x := int16(r.GetIntegerWithDefault("x", 0))
		y := int16(r.GetIntegerWithDefault("y", 0))
		reactorTime := uint32(r.GetIntegerWithDefault("reactorTime", 0))
		name := r.GetString("name", "")
		fd := byte(r.GetIntegerWithDefault("f", 0))
		results = append(results, Reactor{
			id:              id,
			name:            name,
			x:               x,
			y:               y,
			delay:           reactorTime * 1000,
			facingDirection: fd,
		})
	}
	return results
}

func getBackgroundTypes(exml *xml.Node) []BackgroundType {
	results := make([]BackgroundType, 0)
	bts, err := exml.ChildByName("back")
	if err != nil {
		return results
	}
	for _, bt := range bts.ChildNodes {
		layerNum, err := strconv.Atoi(bt.Name)
		if err != nil {
			continue
		}
		backgroundType := bt.GetIntegerWithDefault("type", 0)
		results = append(results, BackgroundType{layerNumber: uint32(layerNum), backgroundType: uint32(backgroundType)})
	}

	return results
}

func isBoat(exml *xml.Node) bool {
	_, err := exml.ChildByName("shipObj")
	return err != nil
}

func getClock(exml *xml.Node) bool {
	_, err := exml.ChildByName("clock")
	return err != nil
}

func getStreetName(mapId uint32) string {
	md, err := readMapNameData(mapId)
	if err != nil {
		return ""
	}
	return md.GetString("streetName", "")
}

func getPlaceName(mapId uint32) string {
	md, err := readMapNameData(mapId)
	if err != nil {
		return ""
	}
	return md.GetString("mapName", "")
}

func getSeats(exml *xml.Node) uint32 {
	s, err := exml.ChildByName("seat")
	if err != nil {
		return 0
	}
	return uint32(len(s.PointNodes))
}

func getAreas(exml *xml.Node) []Rectangle {
	results := make([]Rectangle, 0)
	a, err := exml.ChildByName("area")
	if err != nil {
		return results
	}
	for _, area := range a.ChildNodes {
		x1 := int16(area.GetIntegerWithDefault("x1", 0))
		y1 := int16(area.GetIntegerWithDefault("y1", 0))
		x2 := int16(area.GetFloatWithDefault("x2", 0))
		y2 := int16(area.GetIntegerWithDefault("y2", 0))
		result := Rectangle{
			x:      x1,
			y:      y1,
			width:  x2 - x1,
			height: y2 - y1,
		}
		results = append(results, result)
	}
	return results
}

func getFootholdTree(exml *xml.Node) *FootholdTree {
	footholds := make([]Foothold, 0)
	var lx int16
	var ly int16
	var ux int16
	var uy int16

	fr, err := exml.ChildByName("foothold")
	if err == nil {
		for _, fc := range fr.ChildNodes {
			for _, fh := range fc.ChildNodes {
				x1 := int16(fh.GetIntegerWithDefault("x1", 0))
				y1 := int16(fh.GetIntegerWithDefault("y1", 0))
				x2 := int16(fh.GetFloatWithDefault("x2", 0))
				y2 := int16(fh.GetIntegerWithDefault("y2", 0))
				id, err := strconv.Atoi(fh.Name)
				if err != nil {
					continue
				}
				foothold := Foothold{
					id:      uint32(id),
					firstX:  x1,
					firstY:  y1,
					secondX: x2,
					secondY: y2,
				}
				if x1 < lx {
					lx = x1
				}
				if x2 > ux {
					ux = x2
				}
				if y1 < ly {
					ly = y1
				}
				if y2 > uy {
					uy = y2
				}
				footholds = append(footholds, foothold)
			}
		}
	}
	return NewFootholdTree(lx, ly, ux, uy).Insert(footholds)
}

func getMapArea(exml *xml.Node, i *xml.Node) Rectangle {
	bounds := make([]int16, 4)
	bounds[0] = int16(i.GetIntegerWithDefault("VRTop", 0))
	bounds[1] = int16(i.GetIntegerWithDefault("VRBottom", 0))

	if bounds[0] == bounds[1] {
		mm, err := exml.ChildByName("miniMap")
		if err == nil {
			bounds[0] = int16(mm.GetIntegerWithDefault("centerX", 0) * -1)
			bounds[1] = int16(mm.GetIntegerWithDefault("centerY", 0) * -1)
			bounds[2] = int16(mm.GetIntegerWithDefault("height", 0))
			bounds[3] = int16(mm.GetIntegerWithDefault("width", 0))
			return Rectangle{
				x:      bounds[0],
				y:      bounds[1],
				width:  bounds[2],
				height: bounds[3],
			}
		} else {
			dist := 1 << 18
			return Rectangle{
				x:      int16(-dist / 2),
				y:      int16(-dist / 2),
				width:  int16(dist),
				height: int16(dist),
			}
		}
	} else {
		bounds[2] = int16(i.GetIntegerWithDefault("VRLeft", 0))
		bounds[3] = int16(i.GetIntegerWithDefault("VRRight", 0))
		return Rectangle{
			x:      bounds[2],
			y:      bounds[0],
			width:  bounds[3] - bounds[2],
			height: bounds[1] - bounds[0],
		}
	}

}

func getTimeMob(i *xml.Node) *TimeMob {
	tm, err := i.ChildByName("timeMob")
	if err != nil {
		return nil
	}
	id := uint32(tm.GetIntegerWithDefault("id", 0))
	message := tm.GetString("message", "")
	return &TimeMob{
		id:      id,
		message: message,
	}
}

var portalId uint32

func getPortals(exml *xml.Node) []Portal {
	portals := make([]Portal, 0)
	p, err := exml.ChildByName("portal")
	if err != nil {
		return portals
	}
	for _, c := range p.ChildNodes {
		var pid uint32
		pt := uint8(c.GetIntegerWithDefault("pt", 0))
		if pt == PortalTypeDoor {
			pid = atomic.AddUint32(&portalId, 1)
		} else {
			pstr, err := strconv.Atoi(c.Name)
			if err != nil {
				continue
			}
			pid = uint32(pstr)
		}

		portal := Portal{
			id:          pid,
			name:        c.GetString("pn", ""),
			target:      c.GetString("tn", ""),
			portalType:  pt,
			x:           int16(c.GetIntegerWithDefault("x", 0)),
			y:           int16(c.GetFloatWithDefault("y", 0)),
			targetMapId: uint32(c.GetIntegerWithDefault("tm", 0)),
			scriptName:  c.GetString("script", ""),
		}
		portals = append(portals, portal)
	}
	return portals
}
