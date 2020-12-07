package com.atlas.mis.model;

import java.util.List;
import java.util.Map;

public record MonsterData(String name, int hp, int mp, int exp, int level, int paDamage, int pdDamage,
                          int maDamage, int mdDamage, boolean isFriendly, int removeAfter, boolean isBoss,
                          boolean isExplosiveReward, boolean isFFALoot, boolean isUndead, int buffToGive, int cp,
                          boolean removeOnMiss, boolean changeable, Map<String, Integer> animationTimes,
                          Map<Element, ElementalEffectiveness> resistances, List<LoseItem> loseItemList,
                          List<SkillData> skills, List<Integer> revives, byte tagColor,
                          byte tagBackgroundColor, int fixedStance, boolean firstAttack, BanishInfo banish,
                          int dropPeriod, SelfDestruction selfDestruction, CoolDamage cool) {
}
