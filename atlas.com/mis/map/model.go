package _map

import (
	"atlas-mis/point"
	"math"
	"sort"
)

type Model struct {
	id                uint32
	name              string
	streetName        string
	returnMapId       uint32
	monsterRate       float64
	onFirstUserEnter  string
	onUserEnter       string
	fieldLimit        uint32
	mobInterval       uint32
	portals           []Portal
	timeMob           *TimeMob
	mapArea           Rectangle
	footholdTree      *FootholdTree
	areas             []Rectangle
	seats             uint32
	clock             bool
	everLast          bool
	town              bool
	decHp             uint32
	protectItem       uint32
	forcedReturnMapId uint32
	boat              bool
	timeLimit         int32
	fieldType         uint32
	mobCapacity       uint32
	recovery          float64
	backgroundTypes   []BackgroundType
	xLimit            XLimit
	reactors          []Reactor
	npcs              []NPC
	monsters          []Monster
}

type Portal struct {
	id          uint32
	name        string
	target      string
	portalType  uint8
	x           int16
	y           int16
	targetMapId uint32
	scriptName  string
}

type TimeMob struct {
	id      uint32
	message string
}

type Rectangle struct {
	x      int16
	y      int16
	width  int16
	height int16
}

func (r Rectangle) contains(ret point.Model) bool {
	w := r.width
	h := r.height
	if (w | h) < 0 {
		// At least one of the dimensions is negative...
		return false
	}
	// Note: if either dimension is zero, tests below must return false...
	x := r.x
	y := r.y

	if ret.X() < x || ret.Y() < y {
		return false
	}
	w += x
	h += y
	//    overflow || intersect
	return (w < x || w > ret.X()) &&
		(h < y || h > ret.Y())
}

type FootholdTree struct {
	northWest *FootholdTree
	northEast *FootholdTree
	southWest *FootholdTree
	southEast *FootholdTree
	footholds []Foothold
	p1X       int16
	p1Y       int16
	p2X       int16
	p2Y       int16
	centerX   int16
	centerY   int16
	depth     uint32
	maxDropX  int16
	minDropX  int16
}

func (f *FootholdTree) findBelow(initial *point.Model) *Foothold {
	relevants := f.GetRelevant(initial)
	matches := make([]Foothold, 0)

	for _, fh := range relevants {
		if fh.firstX <= initial.X() && fh.secondX >= initial.X() {
			matches = append(matches, fh)
		}
	}
	sort.Slice(matches, func(i, j int) bool {
		if matches[i].secondY < matches[j].firstY {
			return true
		}
		return false
	})
	for _, fh := range matches {
		if !fh.isWall() {
			if fh.firstY != fh.secondY {
				s1 := math.Abs(float64(fh.secondY - fh.firstY))
				s2 := math.Abs(float64(fh.secondX - fh.firstX))
				s4 := math.Abs(float64(initial.X() - fh.firstX))
				alpha := math.Atan(s2 / s1)
				beta := math.Atan(s1 / s2)
				s5 := math.Cos(alpha) * (s4 / math.Cos(beta))
				var calcY int16
				if fh.secondY < fh.firstY {
					calcY = fh.firstY - int16(s5)
				} else {
					calcY = fh.firstY + int16(s5)
				}
				if calcY >= initial.Y() {
					return &fh
				}
			} else {
				if fh.firstY >= initial.Y() {
					return &fh
				}
			}
		}
	}
	return nil
}

func (f *FootholdTree) GetRelevant(point *point.Model) []Foothold {
	results := make([]Foothold, 0)
	results = append(results, f.footholds...)

	if f.northWest != nil {
		if point.X() <= f.centerX && point.Y() <= f.centerY {
			results = append(results, f.northWest.GetRelevant(point)...)
		} else if point.X() > f.centerX && point.Y() <= f.centerY {
			results = append(results, f.northEast.GetRelevant(point)...)
		} else if point.X() <= f.centerX && point.Y() > f.centerY {
			results = append(results, f.southWest.GetRelevant(point)...)
		} else {
			results = append(results, f.southEast.GetRelevant(point)...)
		}
	}
	return results
}

type Foothold struct {
	id      uint32
	firstX  int16
	firstY  int16
	secondX int16
	secondY int16
}

func (f Foothold) isWall() bool {
	return f.firstX == f.secondX
}

type BackgroundType struct {
	layerNumber    uint32
	backgroundType uint32
}

type XLimit struct {
	min uint32
	max uint32
}

type Reactor struct {
	id              string
	name            string
	x               int16
	y               int16
	delay           uint32
	facingDirection byte
}

type NPC struct {
	objectId uint32
	id       uint32
	name     string
	cy       int16
	f        uint32
	fh       uint16
	rx0      int16
	rx1      int16
	x        int16
	y        int16
	hide     bool
}

type Monster struct {
	objectId uint32
	id       uint32
	mobTime  uint32
	team     int32
	cy       int16
	f        uint32
	fh       uint16
	rx0      int16
	rx1      int16
	x        int16
	y        int16
	hide     bool
}
