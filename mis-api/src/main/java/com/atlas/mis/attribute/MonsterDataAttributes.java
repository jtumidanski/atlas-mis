package com.atlas.mis.attribute;

import rest.AttributeResult;

public record MonsterDataAttributes(String name, Integer hp, Integer mp, Integer experience, Integer level, Integer paDamage,
                                    Integer pdDamage, Integer maDamage, Integer mdDamage, Boolean friendly, Integer removeAfter,
                                    Boolean boss, Boolean explosiveReward, Boolean ffaLoot, Boolean undead, Integer buffToGive,
                                    Integer carnivalPoint, Boolean removeOnMiss, Boolean changeable, Byte tagColor,
                                    Byte tagBackgroundColor, Integer fixedStance, Boolean firstAttack, Integer dropPeriod)
      implements AttributeResult {
}
