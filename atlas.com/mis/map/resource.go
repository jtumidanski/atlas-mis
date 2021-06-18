package _map

import (
	"atlas-mis/json"
	"atlas-mis/map/monster"
	point2 "atlas-mis/map/point"
	"atlas-mis/npc"
	"atlas-mis/point"
	"atlas-mis/portal"
	"atlas-mis/reactor"
	"atlas-mis/rest/resource"
	"github.com/gorilla/mux"
	"github.com/sirupsen/logrus"
	"net/http"
	"strconv"
)

type MapHandlerFunc func(mapId uint32) http.HandlerFunc

func WithMap(l logrus.FieldLogger) func(next MapHandlerFunc) http.HandlerFunc {
	return func(next MapHandlerFunc) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			vars := mux.Vars(r)
			mapId, err := strconv.Atoi(vars["mapId"])
			if err != nil {
				l.WithError(err).Errorf("Error parsing mapId as uint32")
				w.WriteHeader(http.StatusBadRequest)
				return
			}
			next(uint32(mapId))(w, r)
		}
	}
}

type MapPortalHandlerFunc func(portalId uint32) MapHandlerFunc

func WithMapPortal(l logrus.FieldLogger) func(next MapPortalHandlerFunc) http.HandlerFunc {
	return func(next MapPortalHandlerFunc) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			vars := mux.Vars(r)
			portalId, err := strconv.Atoi(vars["portalId"])
			if err != nil {
				l.WithError(err).Errorf("Error parsing portalId as uint32")
				w.WriteHeader(http.StatusBadRequest)
				return
			}
			WithMap(l)(next(uint32(portalId)))(w, r)
		}
	}
}

type MapNPCHandlerFunc func(npcId uint32) MapHandlerFunc

func WithMapNPC(l logrus.FieldLogger) func(next MapNPCHandlerFunc) http.HandlerFunc {
	return func(next MapNPCHandlerFunc) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			vars := mux.Vars(r)
			npcId, err := strconv.Atoi(vars["npcId"])
			if err != nil {
				l.WithError(err).Errorf("Error parsing npcId as uint32")
				w.WriteHeader(http.StatusBadRequest)
				return
			}
			WithMap(l)(next(uint32(npcId)))(w, r)
		}
	}
}

func HandleGetMapRequest(l logrus.FieldLogger) MapHandlerFunc {
	return func(mapId uint32) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			m, err := GetRegistry().GetMap(mapId)
			if err != nil {
				l.WithError(err).Debugf("Unable to locate map %d.", mapId)
				w.WriteHeader(http.StatusNotFound)
				return
			}

			result := DataContainer{Data: makeMap(m)}

			w.WriteHeader(http.StatusOK)
			err = json.ToJSON(result, w)
			if err != nil {
				l.WithError(err).Errorf("Writing response.")
			}
			return
		}
	}
}

func makeMap(m *Model) DataBody {
	return DataBody{
		Id:   strconv.Itoa(int(m.id)),
		Type: "",
		Attributes: Attributes{
			Name:              m.name,
			StreetName:        m.streetName,
			ReturnMapId:       m.returnMapId,
			MonsterRate:       m.monsterRate,
			OnFirstUserEnter:  m.onFirstUserEnter,
			OnUserEnter:       m.onUserEnter,
			FieldLimit:        m.fieldLimit,
			MobInterval:       m.mobInterval,
			Seats:             m.seats,
			Clock:             m.clock,
			EverLast:          m.everLast,
			Town:              m.town,
			DecHP:             m.decHp,
			ProtectItem:       m.protectItem,
			ForcedReturnMapId: m.forcedReturnMapId,
			Boat:              m.boat,
			TimeLimit:         m.timeLimit,
			FieldType:         m.fieldType,
			MobCapacity:       m.mobCapacity,
			Recovery:          m.recovery,
			MapArea:           makeRectangleAttributes(m.mapArea),
			Areas:             makeRectangleAttributesList(m.areas),
			BackgroundTypes:   makeBackgroundTypes(m.backgroundTypes),
		},
	}
}

func makeBackgroundTypes(types []BackgroundType) []backgroundTypeAttributes {
	results := make([]backgroundTypeAttributes, 0)
	for _, t := range types {
		results = append(results, backgroundTypeAttributes{
			LayerNumber:    t.layerNumber,
			BackgroundType: t.backgroundType,
		})
	}
	return results
}

func makeRectangleAttributesList(areas []Rectangle) []rectangleAttributes {
	results := make([]rectangleAttributes, 0)
	for _, area := range areas {
		results = append(results, makeRectangleAttributes(area))
	}
	return results
}

func makeRectangleAttributes(area Rectangle) rectangleAttributes {
	return rectangleAttributes{
		X:      area.x,
		Y:      area.y,
		Width:  area.width,
		Height: area.height,
	}
}

func HandleGetMapPortalsRequest(l logrus.FieldLogger) MapHandlerFunc {
	return func(mapId uint32) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			m, err := GetRegistry().GetMap(mapId)
			if err != nil {
				l.WithError(err).Debugf("Unable to locate map %d.", mapId)
				w.WriteHeader(http.StatusNotFound)
				return
			}

			result := makePortalListResult(m.portals)

			w.WriteHeader(http.StatusOK)
			err = json.ToJSON(result, w)
			if err != nil {
				l.WithError(err).Errorf("Writing response.")
			}
			return
		}
	}
}

func HandleGetMapPortalsByNameRequest(l logrus.FieldLogger) MapHandlerFunc {
	return func(mapId uint32) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			vars := mux.Vars(r)
			portalName := vars["name"]

			m, err := GetRegistry().GetMap(mapId)
			if err != nil {
				l.WithError(err).Debugf("Unable to locate map %d.", mapId)
				w.WriteHeader(http.StatusNotFound)
				return
			}

			result := makePortalListResult(m.portals, PortalNameFilter(portalName))

			w.WriteHeader(http.StatusOK)
			err = json.ToJSON(result, w)
			if err != nil {
				l.WithError(err).Errorf("Writing response.")
			}
			return
		}
	}
}

func HandleGetMapPortalRequest(l logrus.FieldLogger) MapPortalHandlerFunc {
	return func(portalId uint32) MapHandlerFunc {
		return func(mapId uint32) http.HandlerFunc {
			return func(w http.ResponseWriter, r *http.Request) {
				m, err := GetRegistry().GetMap(mapId)
				if err != nil {
					l.WithError(err).Debugf("Unable to locate map %d.", mapId)
					w.WriteHeader(http.StatusNotFound)
					return
				}

				result := makePortalListResult(m.portals, PortalIdFilter(portalId))

				w.WriteHeader(http.StatusOK)
				err = json.ToJSON(result, w)
				if err != nil {
					l.WithError(err).Errorf("Writing response.")
				}
				return
			}
		}
	}
}

func makePortalListResult(portals []Portal, filters ...PortalFilter) *portal.DataListContainer {
	result := &portal.DataListContainer{Data: make([]portal.DataBody, 0)}
	for _, p := range portals {
		ok := true
		for _, filter := range filters {
			if !filter(p) {
				ok = false
				break
			}
		}
		if ok {
			result.Data = append(result.Data, makePortal(p))
		}
	}
	return result
}

type PortalFilter func(p Portal) bool

func PortalIdFilter(portalId uint32) PortalFilter {
	return func(p Portal) bool {
		return p.id == portalId
	}
}

func PortalNameFilter(portalName string) PortalFilter {
	return func(p Portal) bool {
		return p.name == portalName
	}
}

func makePortal(p Portal) portal.DataBody {
	return portal.DataBody{
		Id:   strconv.Itoa(int(p.id)),
		Type: "",
		Attributes: portal.Attributes{
			Name:        p.name,
			Target:      p.target,
			Type:        p.portalType,
			X:           p.x,
			Y:           p.y,
			TargetMapId: p.targetMapId,
			ScriptName:  p.scriptName,
		},
	}
}

func HandleGetMapReactorsRequest(l logrus.FieldLogger) MapHandlerFunc {
	return func(mapId uint32) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			m, err := GetRegistry().GetMap(mapId)
			if err != nil {
				l.WithError(err).Debugf("Unable to locate map %d.", mapId)
				w.WriteHeader(http.StatusNotFound)
				return
			}

			result := &reactor.DataListContainer{Data: make([]reactor.DataBody, 0)}
			for _, r := range m.reactors {
				result.Data = append(result.Data, makeReactor(r))
			}

			w.WriteHeader(http.StatusOK)
			err = json.ToJSON(result, w)
			if err != nil {
				l.WithError(err).Errorf("Writing response.")
			}
			return
		}
	}
}

func makeReactor(r Reactor) reactor.DataBody {
	return reactor.DataBody{
		Id:   r.id,
		Type: "",
		Attributes: reactor.Attributes{
			Name:            r.name,
			X:               r.x,
			Y:               r.y,
			Delay:           r.delay,
			FacingDirection: r.facingDirection,
		},
	}
}

func HandleGetMapNPCsByObjectIdRequest(l logrus.FieldLogger) MapHandlerFunc {
	return func(mapId uint32) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			vars := mux.Vars(r)
			objectId, err := strconv.Atoi(vars["objectId"])
			if err != nil {
				l.WithError(err).Errorf("Error parsing objectId as uint32")
				w.WriteHeader(http.StatusBadRequest)
				return
			}

			m, err := GetRegistry().GetMap(mapId)
			if err != nil {
				l.WithError(err).Debugf("Unable to locate map %d.", mapId)
				w.WriteHeader(http.StatusNotFound)
				return
			}

			result := makeNPCListResult(m.npcs, NPCObjectIdFilter(uint32(objectId)))

			w.WriteHeader(http.StatusOK)
			err = json.ToJSON(result, w)
			if err != nil {
				l.WithError(err).Errorf("Writing response.")
			}
			return
		}
	}
}

func HandleGetMapNPCsRequest(l logrus.FieldLogger) MapHandlerFunc {
	return func(mapId uint32) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			m, err := GetRegistry().GetMap(mapId)
			if err != nil {
				l.WithError(err).Debugf("Unable to locate map %d.", mapId)
				w.WriteHeader(http.StatusNotFound)
				return
			}

			result := makeNPCListResult(m.npcs)

			w.WriteHeader(http.StatusOK)
			err = json.ToJSON(result, w)
			if err != nil {
				l.WithError(err).Errorf("Writing response.")
			}
			return
		}
	}
}

func makeNPCListResult(npcs []NPC, filters ...NPCFilter) *npc.DataListContainer {
	result := &npc.DataListContainer{Data: make([]npc.DataBody, 0)}
	for _, n := range npcs {
		ok := true
		for _, filter := range filters {
			if !filter(n) {
				ok = false
				break
			}
		}
		if ok {
			result.Data = append(result.Data, makeNPC(n))
		}
	}
	return result
}

type NPCFilter func(n NPC) bool

func NPCIdFilter(id uint32) NPCFilter {
	return func(n NPC) bool {
		return n.id == id
	}
}

func NPCObjectIdFilter(id uint32) NPCFilter {
	return func(n NPC) bool {
		return n.objectId == id
	}
}

func HandleGetMapNPCRequest(l logrus.FieldLogger) MapNPCHandlerFunc {
	return func(npcId uint32) MapHandlerFunc {
		return func(mapId uint32) http.HandlerFunc {
			return func(w http.ResponseWriter, r *http.Request) {
				m, err := GetRegistry().GetMap(mapId)
				if err != nil {
					l.WithError(err).Debugf("Unable to locate map %d.", mapId)
					w.WriteHeader(http.StatusNotFound)
					return
				}

				result := makeNPCListResult(m.npcs, NPCIdFilter(npcId))

				w.WriteHeader(http.StatusOK)
				err = json.ToJSON(result, w)
				if err != nil {
					l.WithError(err).Errorf("Writing response.")
				}
				return
			}
		}
	}
}

func makeNPC(n NPC) npc.DataBody {
	return npc.DataBody{
		Id:   strconv.Itoa(int(n.objectId)),
		Type: "",
		Attributes: npc.Attributes{
			Id:   n.id,
			Name: n.name,
			CY:   n.cy,
			F:    n.f,
			FH:   n.fh,
			RX0:  n.rx0,
			RX1:  n.rx1,
			X:    n.x,
			Y:    n.y,
			Hide: n.hide,
		},
	}
}

func HandleGetMapMonstersRequest(l logrus.FieldLogger) MapHandlerFunc {
	return func(mapId uint32) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			m, err := GetRegistry().GetMap(mapId)
			if err != nil {
				l.WithError(err).Debugf("Unable to locate map %d.", mapId)
				w.WriteHeader(http.StatusNotFound)
				return
			}

			result := &monster.DataListContainer{Data: make([]monster.DataBody, 0)}
			for _, n := range m.monsters {
				result.Data = append(result.Data, makeMonster(n))
			}

			w.WriteHeader(http.StatusOK)
			err = json.ToJSON(result, w)
			if err != nil {
				l.WithError(err).Errorf("Writing response.")
			}
			return
		}
	}
}

func makeMonster(n Monster) monster.DataBody {
	return monster.DataBody{
		Id:   strconv.Itoa(int(n.objectId)),
		Type: "",
		Attributes: monster.Attributes{
			Id:      n.id,
			MobTime: n.mobTime,
			Team:    n.team,
			CY:      n.cy,
			F:       n.f,
			FH:      n.fh,
			RX0:     n.rx0,
			RX1:     n.rx1,
			X:       n.x,
			Y:       n.y,
			Hide:    n.hide,
		},
	}
}

func HandleGetMapDropPositionRequest(l logrus.FieldLogger) MapHandlerFunc {
	return func(mapId uint32) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			li := &DropPositionInputDataContainer{}
			err := json.FromJSON(li, r.Body)
			if err != nil {
				l.WithError(err).Errorf("Deserializing input.")
				w.WriteHeader(http.StatusBadRequest)
				err = json.ToJSON(&resource.GenericError{Message: err.Error()}, w)
				if err != nil {
					l.WithError(err).Fatalf("Writing error message.")
				}
				return
			}

			attr := li.Data.Attributes
			p := calcDropPos(mapId, point.NewModel(attr.InitialX, attr.InitialY), point.NewModel(attr.FallbackX, attr.FallbackY))

			result := &point2.DataContainer{Data: point2.DataBody{
				Id:   "0",
				Type: "",
				Attributes: point2.Attributes{
					X: p.X(),
					Y: p.Y(),
				},
			}}

			w.WriteHeader(http.StatusOK)
			err = json.ToJSON(result, w)
			if err != nil {
				l.WithError(err).Errorf("Writing response.")
			}
			return
		}
	}
}

type DropPositionInputDataContainer struct {
	Data DropPositionData `json:"data"`
}

type DropPositionData struct {
	Id         string                 `json:"id"`
	Type       string                 `json:"type"`
	Attributes DropPositionAttributes `json:"attributes"`
}

type DropPositionAttributes struct {
	InitialX  int16 `json:"initialX"`
	InitialY  int16 `json:"initialY"`
	FallbackX int16 `json:"fallbackX"`
	FallbackY int16 `json:"fallbackY"`
}
