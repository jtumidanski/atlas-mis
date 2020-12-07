package com.atlas.mis;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.atlas.mis.model.MonsterData;
import com.atlas.mis.processor.MonsterProcessor;
import com.atlas.shared.wz.MapleData;

public class MonsterDataRegistry {
   private static final Object lock = new Object();

   private static volatile MonsterDataRegistry instance;

   protected final Map<Integer, MonsterData> monsterDataMap;

   protected Set<Integer> hpBarBosses;

   public static MonsterDataRegistry getInstance() {
      MonsterDataRegistry result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new MonsterDataRegistry();
               instance = result;
            }
         }
      }
      return result;
   }

   private MonsterDataRegistry() {
      monsterDataMap = new ConcurrentHashMap<>();
   }

   public Optional<MonsterData> getMonsterData(int monsterId) {
      if (!monsterDataMap.containsKey(monsterId)) {
         MonsterProcessor.produceMonsterData(monsterId)
               .ifPresent(monster -> monsterDataMap.put(monsterId, monster));
      }
      return Optional.ofNullable(monsterDataMap.getOrDefault(monsterId, null));
   }

   public Set<Integer> getHpBarBosses() {
      if (hpBarBosses == null) {
         hpBarBosses = DataRegistry.getInstance()
               .getUiSource()
               .data("UIWindow.img")
               .flatMap(data -> data.childByPath("MobGage/Mob"))
               .map(MapleData::children)
               .orElse(Collections.emptyList())
               .stream()
               .map(MapleData::name)
               .map(Integer::parseInt)
               .collect(Collectors.toSet());
      }
      return hpBarBosses;
   }
}
