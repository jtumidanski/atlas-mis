package monster

import (
	"atlas-mis/json"
	"github.com/gorilla/mux"
	"github.com/sirupsen/logrus"
	"net/http"
	"strconv"
)

func HandleGetMonsterRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		monsterId, err := strconv.Atoi(vars["monsterId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing monsterId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		m, err := GetRegistry().GetMonster(uint32(monsterId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate monster %d.", monsterId)
			w.WriteHeader(http.StatusNotFound)
			return
		}
		
		result := &DataContainer{Data: DataBody{
			Id:         strconv.Itoa(int(m.id)),
			Type:       "",
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

func HandleGetMonsterLoseItemsRequest(l logrus.FieldLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		monsterId, err := strconv.Atoi(vars["monsterId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing monsterId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		m, err := GetRegistry().GetMonster(uint32(monsterId))
		if err != nil {
			l.WithError(err).Debugf("Unable to locate monster %d.", monsterId)
			w.WriteHeader(http.StatusNotFound)
			return
		}

		result := &loseItemDataListContainer{Data: make([]loseItemDatBody, 0)}
		for _, li := range m.loseItems {
			lir := loseItemDatBody{
				Id:         strconv.Itoa(int(li.itemId)),
				Type:       "",
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
