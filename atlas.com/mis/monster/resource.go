package monster

import (
	"atlas-mis/json"
	"atlas-mis/rest"
	"github.com/gorilla/mux"
	"github.com/opentracing/opentracing-go"
	"github.com/sirupsen/logrus"
	"net/http"
	"strconv"
)

const (
	getMonster          = "get_monster"
	getMonsterLoseItems = "get_monster_lose_items"
)

func InitResource(router *mux.Router, l logrus.FieldLogger) {
	r := router.PathPrefix("/monsters").Subrouter()
	r.HandleFunc("/{monsterId}", registerGetMonsterRequest(l)).Methods(http.MethodGet)
	r.HandleFunc("/{monsterId}/loseItems", registerGetMonsterLoseItemsRequest(l)).Methods(http.MethodGet)
}

func registerGetMonsterLoseItemsRequest(l logrus.FieldLogger) http.HandlerFunc {
	return rest.RetrieveSpan(getMonsterLoseItems, func(span opentracing.Span) http.HandlerFunc {
		return parseMonsterId(l, func(monsterId uint32) http.HandlerFunc {
			return handleGetMonsterLoseItemsRequest(l)(span)(monsterId)
		})
	})
}

func registerGetMonsterRequest(l logrus.FieldLogger) http.HandlerFunc {
	return rest.RetrieveSpan(getMonster, func(span opentracing.Span) http.HandlerFunc {
		return parseMonsterId(l, func(monsterId uint32) http.HandlerFunc {
			return handleGetMonsterRequest(l)(span)(monsterId)
		})
	})
}

type monsterIdHandler func(monsterId uint32) http.HandlerFunc

func parseMonsterId(l logrus.FieldLogger, next monsterIdHandler) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		monsterId, err := strconv.Atoi(vars["monsterId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing monsterId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}
		next(uint32(monsterId))(w, r)
	}
}

func handleGetMonsterRequest(l logrus.FieldLogger) func(span opentracing.Span) func(monsterId uint32) http.HandlerFunc {
	return func(span opentracing.Span) func(monsterId uint32) http.HandlerFunc {
		return func(monsterId uint32) http.HandlerFunc {
			return func(w http.ResponseWriter, r *http.Request) {
				m, err := GetRegistry().GetMonster(monsterId)
				if err != nil {
					l.WithError(err).Debugf("Unable to locate monster %d.", monsterId)
					w.WriteHeader(http.StatusNotFound)
					return
				}

				result := &DataContainer{Data: DataBody{
					Id:   strconv.Itoa(int(m.id)),
					Type: "",
					Attributes: Attributes{
						Name:               m.name,
						HP:                 m.hp,
						MP:                 m.mp,
						Experience:         m.experience,
						Level:              m.level,
						WeaponAttack:       m.weaponAttack,
						WeaponDefense:      m.weaponDefense,
						MagicAttack:        m.magicAttack,
						MagicDefense:       m.magicDefense,
						Friendly:           m.friendly,
						RemoveAfter:        m.removeAfter,
						Boss:               m.boss,
						ExplosiveReward:    m.explosiveReward,
						FFALoot:            m.ffaLoot,
						Undead:             m.undead,
						BuffToGive:         m.buffToGive,
						CP:                 m.cp,
						RemoveOnMiss:       m.removeOnMiss,
						Changeable:         m.changeable,
						AnimationTimes:     m.animationTimes,
						Resistances:        m.resistances,
						LoseItems:          makeLoseItems(m.loseItems),
						Skills:             makeSkills(m.skills),
						Revives:            m.revives,
						TagColor:           m.tagColor,
						TagBackgroundColor: m.tagBackgroundColor,
						FixedStance:        m.fixedStance,
						FirstAttack:        m.firstAttack,
						Banish:             makeBanish(m.banish),
						DropPeriod:         m.dropPeriod,
						SelfDestruction:    makeSelfDestruction(m.selfDestruction),
						CoolDamage:         makeCoolDamage(m.coolDamage),
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
}

func makeCoolDamage(c *CoolDamage) coolDamage {
	if c == nil {
		return coolDamage{}
	}
	return coolDamage{
		Damage:      c.damage,
		Probability: c.probability,
	}
}

func makeSelfDestruction(d *SelfDestruction) selfDestruction {
	if d == nil {
		return selfDestruction{}
	}
	return selfDestruction{
		Action:      d.action,
		RemoveAfter: d.removeAfter,
		HP:          d.hp,
	}
}

func makeBanish(b *Banish) banish {
	if b == nil {
		return banish{}
	}
	return banish{
		Message:    b.message,
		MapId:      b.mapId,
		PortalName: b.portalName,
	}
}

func makeSkills(skills []Skill) []skill {
	result := make([]skill, 0)
	for _, s := range skills {
		result = append(result, skill{
			Id:    s.id,
			Level: s.level,
		})
	}
	return result
}

func makeLoseItems(items []LoseItem) []loseItem {
	result := make([]loseItem, 0)
	for _, item := range items {
		result = append(result, loseItem{
			Id:     item.itemId,
			Chance: item.chance,
			X:      item.x,
		})
	}
	return result
}

func handleGetMonsterLoseItemsRequest(l logrus.FieldLogger) func(span opentracing.Span) func(monsterId uint32) http.HandlerFunc {
	return func(span opentracing.Span) func(monsterId uint32) http.HandlerFunc {
		return func(monsterId uint32) http.HandlerFunc {
			return func(w http.ResponseWriter, r *http.Request) {
				m, err := GetRegistry().GetMonster(monsterId)
				if err != nil {
					l.WithError(err).Debugf("Unable to locate monster %d.", monsterId)
					w.WriteHeader(http.StatusNotFound)
					return
				}

				result := &loseItemDataListContainer{Data: make([]loseItemDatBody, 0)}
				for _, li := range m.loseItems {
					lir := loseItemDatBody{
						Id:   strconv.Itoa(int(li.itemId)),
						Type: "",
						Attributes: loseItemAttributes{
							Chance: li.chance,
							X:      li.x,
						},
					}
					result.Data = append(result.Data, lir)
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
}
