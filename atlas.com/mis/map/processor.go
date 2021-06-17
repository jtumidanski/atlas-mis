package _map

import (
	"atlas-mis/point"
	"math"
)

type FootholdTreeConfigurator func(f *FootholdTree)

func NewFootholdTree(lx int16, ly int16, ux int16, uy int16, configurations ...FootholdTreeConfigurator) *FootholdTree {
	p1x := lx
	p1y := ly
	p2x := ux
	p2y := uy
	centerx := int16(math.Round(float64(ux-lx) / 2))
	centery := int16(math.Round(float64(uy-ly) / 2))
	ft := &FootholdTree{
		northWest: nil,
		northEast: nil,
		southWest: nil,
		southEast: nil,
		footholds: make([]Foothold, 0),
		p1:        point.NewModel(p1x, p1y),
		p2:        point.NewModel(p2x, p2y),
		center:    point.NewModel(centerx, centery),
		depth:     0,
		maxDropX:  0,
		minDropX:  0,
	}

	for _, configurator := range configurations {
		configurator(ft)
	}
	return ft
}

func SetFootholdTreeDepth(depth uint32) FootholdTreeConfigurator {
	return func(f *FootholdTree) {
		f.depth = depth
	}
}

func (f *FootholdTree) Insert(footholds []Foothold) *FootholdTree {
	for _, foothold := range footholds {
		f.InsertSingle(foothold)
	}
	return f
}

func (f *FootholdTree) InsertSingle(foothold Foothold) *FootholdTree {
	if f.depth == 0 {
		if foothold.first.X() > f.maxDropX {
			f.maxDropX = foothold.first.X()
		}
		if foothold.first.X() < f.minDropX {
			f.minDropX = foothold.first.X()
		}
		if foothold.second.X() > f.maxDropX {
			f.maxDropX = foothold.second.X()
		}
		if foothold.second.X() < f.minDropX {
			f.minDropX = foothold.second.X()
		}

	}
	if f.depth == 8 || foothold.first.X() >= f.p1.X() && foothold.second.X() <= f.p2.X() && foothold.first.Y() >= f.p1.Y() && foothold.second.Y() <= f.p2.Y() {
		f.footholds = append(f.footholds, foothold)
	} else {
		if f.northWest == nil {
			f.northWest = NewFootholdTree(f.p1.X(), f.p1.Y(), f.center.X(), f.center.Y(), SetFootholdTreeDepth(f.depth+1))
			f.northEast = NewFootholdTree(f.center.X(), f.p1.Y(), f.p2.X(), f.center.Y(), SetFootholdTreeDepth(f.depth+1))
			f.southWest = NewFootholdTree(f.p1.X(), f.center.Y(), f.center.X(), f.p2.Y(), SetFootholdTreeDepth(f.depth+1))
			f.southEast = NewFootholdTree(f.center.X(), f.center.Y(), f.p2.X(), f.p2.Y(), SetFootholdTreeDepth(f.depth+1))
		}
		if foothold.second.X() <= f.center.X() && foothold.second.Y() <= f.center.Y() {
			f.northWest = f.northWest.InsertSingle(foothold)
		} else if foothold.first.X() > f.center.X() && foothold.second.Y() <= f.center.Y() {
			f.northEast = f.northEast.InsertSingle(foothold)
		} else if foothold.second.X() <= f.center.X() && foothold.first.Y() > f.center.Y() {
			f.southWest = f.southWest.InsertSingle(foothold)
		} else {
			f.southEast = f.southEast.InsertSingle(foothold)
		}
	}
	return f
}

func calcDropPos(mapId uint32, initial *point.Model, fallback *point.Model) *point.Model {
	m, err := GetRegistry().GetMap(mapId)
	if err != nil {
		return fallback
	}

	rp := initial
	if rp.X() < int16(m.xLimit.min) {
		rp = rp.SetX(int16(m.xLimit.min))
	} else if rp.X() > int16(m.xLimit.max) {
		rp = rp.SetX(int16(m.xLimit.max))
	}
	ret := calcPointBelow(m.footholdTree, point.NewModel(rp.X(), rp.Y()-85))
	if ret == nil {
		ret = bSearchDropPos(m.footholdTree, initial, fallback)
	}
	if !m.mapArea.contains(*ret) {
		return fallback
	}
	return ret
}

func calcPointBelow(tree *FootholdTree, initial *point.Model) *point.Model {
	fh := tree.findBelow(initial)
	if fh == nil {
		return nil
	}

	dropY := fh.first.Y()
	if !fh.isWall() && fh.first.Y() != fh.second.Y() {
		s1 := math.Abs(float64(fh.second.Y() - fh.first.Y()))
		s2 := math.Abs(float64(fh.second.X() - fh.first.X()))
		s5 := math.Cos(math.Atan(s2/s1)) * (math.Abs(float64(initial.X()-fh.first.X())) / math.Cos(math.Atan(s1/s2)))
		if fh.second.Y() < fh.first.Y() {
			dropY = fh.first.Y() - int16(s5)
		} else {
			dropY = fh.first.Y() + int16(s5)
		}
	}
	ret := point.NewModel(initial.X(), dropY)
	return ret
}

func bSearchDropPos(tree *FootholdTree, initial *point.Model, fallback *point.Model) *point.Model {
	var dropPos *point.Model
	awayX := fallback.X()
	homeX := initial.X()
	y := initial.Y() - 85

	for math.Abs(float64(homeX-awayX)) > 5 {
		distanceX := awayX - homeX
		dx := distanceX / 2
		searchX := homeX + dx
		res := calcPointBelow(tree, point.NewModel(searchX, y))
		if res != nil {
			awayX = searchX
			dropPos = res
		} else {
			homeX = searchX
		}
	}

	if dropPos != nil {
		return dropPos
	}
	return fallback
}
