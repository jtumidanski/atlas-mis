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
		p1X:       p1x,
		p1Y:       p1y,
		p2X:       p2x,
		p2Y:       p2y,
		centerX:   centerx,
		centerY:   centery,
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
		if foothold.firstX > f.maxDropX {
			f.maxDropX = foothold.firstX
		}
		if foothold.firstX < f.minDropX {
			f.minDropX = foothold.firstX
		}
		if foothold.secondX > f.maxDropX {
			f.maxDropX = foothold.secondX
		}
		if foothold.secondX < f.minDropX {
			f.minDropX = foothold.secondX
		}

	}
	if f.depth == 8 || foothold.firstX >= f.p1X && foothold.secondX <= f.p2X && foothold.firstY >= f.p1Y && foothold.secondY <= f.p2Y {
		f.footholds = append(f.footholds, foothold)
	} else {
		if f.northWest == nil {
			f.northWest = NewFootholdTree(f.p1X, f.p1Y, f.centerX, f.centerY, SetFootholdTreeDepth(f.depth+1))
			f.northEast = NewFootholdTree(f.centerX, f.p1Y, f.p2X, f.centerY, SetFootholdTreeDepth(f.depth+1))
			f.southWest = NewFootholdTree(f.p1X, f.centerY, f.centerX, f.p2Y, SetFootholdTreeDepth(f.depth+1))
			f.southEast = NewFootholdTree(f.centerX, f.centerY, f.p2X, f.p2Y, SetFootholdTreeDepth(f.depth+1))
		}
		if foothold.secondX <= f.centerX && foothold.secondY <= f.centerY {
			f.northWest = f.northWest.InsertSingle(foothold)
		} else if foothold.firstX > f.centerX && foothold.secondY <= f.centerY {
			f.northEast = f.northEast.InsertSingle(foothold)
		} else if foothold.secondX <= f.centerX && foothold.firstY > f.centerY {
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

	dropY := fh.firstY
	if !fh.isWall() && fh.firstY != fh.secondY {
		s1 := math.Abs(float64(fh.secondY - fh.firstY))
		s2 := math.Abs(float64(fh.secondX - fh.firstX))
		s5 := math.Cos(math.Atan(s2/s1)) * (math.Abs(float64(initial.X()-fh.firstX)) / math.Cos(math.Atan(s1/s2)))
		if fh.secondY < fh.firstY {
			dropY = fh.firstY - int16(s5)
		} else {
			dropY = fh.firstY + int16(s5)
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
