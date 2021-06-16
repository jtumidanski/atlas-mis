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

func HandleGetMapRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		mapId, err := strconv.Atoi(vars["mapId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing mapId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		m, err := GetRegistry().GetMap(uint32(mapId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate map %d.", mapId)
			w.WriteHeader(http.StatusNotFound)
			return
		}

		result := DataContainer{Data: DataBody{
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
		}}

		w.WriteHeader(http.StatusOK)
		err = json.ToJSON(result, w)
		if err != nil {
			l.WithError(err).Errorf("Writing response.")
		}
		return
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

func HandleGetMapPortalsRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		mapId, err := strconv.Atoi(vars["mapId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing mapId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		m, err := GetRegistry().GetMap(uint32(mapId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate map %d.", mapId)
			w.WriteHeader(http.StatusNotFound)
			return
		}

		result := &portal.DataListContainer{Data: make([]portal.DataBody, 0)}

		for _, p := range m.portals {
			pa := portal.DataBody{
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
			result.Data = append(result.Data, pa)
		}

		w.WriteHeader(http.StatusOK)
		err = json.ToJSON(result, w)
		if err != nil {
			l.WithError(err).Errorf("Writing response.")
		}
		return
	}
}

func HandleGetMapPortalsByNameRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		mapId, err := strconv.Atoi(vars["mapId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing mapId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		portalName := vars["name"]

		m, err := GetRegistry().GetMap(uint32(mapId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate map %d.", mapId)
			w.WriteHeader(http.StatusNotFound)
			return
		}

		result := &portal.DataListContainer{Data: make([]portal.DataBody, 0)}

		for _, p := range m.portals {
			if p.name != portalName {
				continue
			}

			pa := portal.DataBody{
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
			result.Data = append(result.Data, pa)
		}

		w.WriteHeader(http.StatusOK)
		err = json.ToJSON(result, w)
		if err != nil {
			l.WithError(err).Errorf("Writing response.")
		}
		return
	}
}

func HandleGetMapPortalRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		mapId, err := strconv.Atoi(vars["mapId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing mapId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		portalId, err := strconv.Atoi(vars["portalId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing portalId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		m, err := GetRegistry().GetMap(uint32(mapId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate map %d.", mapId)
			w.WriteHeader(http.StatusNotFound)
			return
		}

		result := &portal.DataListContainer{Data: make([]portal.DataBody, 0)}

		for _, p := range m.portals {
			if p.id != uint32(portalId) {
				continue
			}

			pa := portal.DataBody{
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
			result.Data = append(result.Data, pa)
		}

		w.WriteHeader(http.StatusOK)
		err = json.ToJSON(result, w)
		if err != nil {
			l.WithError(err).Errorf("Writing response.")
		}
		return
	}
}

func HandleGetMapReactorsRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		mapId, err := strconv.Atoi(vars["mapId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing mapId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		m, err := GetRegistry().GetMap(uint32(mapId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate map %d.", mapId)
			w.WriteHeader(http.StatusNotFound)
			return
		}

		result := &reactor.DataListContainer{Data: make([]reactor.DataBody, 0)}
		for _, r := range m.reactors {
			ra := reactor.DataBody{
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
			result.Data = append(result.Data, ra)
		}

		w.WriteHeader(http.StatusOK)
		err = json.ToJSON(result, w)
		if err != nil {
			l.WithError(err).Errorf("Writing response.")
		}
		return
	}
}

func HandleGetMapNPCsByObjectIdRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		mapId, err := strconv.Atoi(vars["mapId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing mapId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		objectId, err := strconv.Atoi(vars["objectId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing objectId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		m, err := GetRegistry().GetMap(uint32(mapId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate map %d.", mapId)
			w.WriteHeader(http.StatusNotFound)
			return
		}

		result := &npc.DataListContainer{Data: make([]npc.DataBody, 0)}
		for _, n := range m.npcs {
			if n.objectId != uint32(objectId) {
				continue
			}
			na := npc.DataBody{
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
			result.Data = append(result.Data, na)
		}

		w.WriteHeader(http.StatusOK)
		err = json.ToJSON(result, w)
		if err != nil {
			l.WithError(err).Errorf("Writing response.")
		}
		return
	}
}

func HandleGetMapNPCsRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		mapId, err := strconv.Atoi(vars["mapId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing mapId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		m, err := GetRegistry().GetMap(uint32(mapId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate map %d.", mapId)
			w.WriteHeader(http.StatusNotFound)
			return
		}

		result := &npc.DataListContainer{Data: make([]npc.DataBody, 0)}
		for _, n := range m.npcs {
			na := npc.DataBody{
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
			result.Data = append(result.Data, na)
		}

		w.WriteHeader(http.StatusOK)
		err = json.ToJSON(result, w)
		if err != nil {
			l.WithError(err).Errorf("Writing response.")
		}
		return
	}
}

func HandleGetMapNPCRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		mapId, err := strconv.Atoi(vars["mapId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing mapId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		npcId, err := strconv.Atoi(vars["npcId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing npcId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		m, err := GetRegistry().GetMap(uint32(mapId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate map %d.", mapId)
			w.WriteHeader(http.StatusNotFound)
			return
		}

		result := &npc.DataListContainer{Data: make([]npc.DataBody, 0)}
		for _, n := range m.npcs {
			if n.id != uint32(npcId) {
				continue
			}
			na := npc.DataBody{
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
			result.Data = append(result.Data, na)
		}

		w.WriteHeader(http.StatusOK)
		err = json.ToJSON(result, w)
		if err != nil {
			l.WithError(err).Errorf("Writing response.")
		}
		return
	}
}

func HandleGetMapMonstersRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		mapId, err := strconv.Atoi(vars["mapId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing mapId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		m, err := GetRegistry().GetMap(uint32(mapId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate map %d.", mapId)
			w.WriteHeader(http.StatusNotFound)
			return
		}

		result := &monster.DataListContainer{Data: make([]monster.DataBody, 0)}
		for _, n := range m.monsters {
			na := monster.DataBody{
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
			result.Data = append(result.Data, na)
		}

		w.WriteHeader(http.StatusOK)
		err = json.ToJSON(result, w)
		if err != nil {
			l.WithError(err).Errorf("Writing response.")
		}
		return
	}
}

func HandleGetMapDropPositionRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		mapId, err := strconv.Atoi(vars["mapId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing mapId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		li := &DropPositionInputDataContainer{}
		err = json.FromJSON(li, r.Body)
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
		p := calcDropPos(uint32(mapId), point.NewModel(attr.InitialX, attr.InitialY), point.NewModel(attr.FallbackX, attr.FallbackY))

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
