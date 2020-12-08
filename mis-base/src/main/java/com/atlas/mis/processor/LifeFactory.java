package com.atlas.mis.processor;

import java.awt.*;
import java.util.Optional;

import com.atlas.mis.DataRegistry;
import com.atlas.mis.builder.LifeBuilder;
import com.atlas.mis.builder.MonsterBuilder;
import com.atlas.mis.builder.NpcBuilder;
import com.atlas.mis.model.Life;
import com.atlas.shared.wz.MapleDataTool;

public final class LifeFactory {
   private LifeFactory() {
   }

   public static Optional<Life> createLife(int objectId, int id, String type, int team, int cy, int f, int fh, int rx0, int rx1,
                                           int x, int y, int hide, int mobTime) {
      LifeBuilder<? extends Life, ?> lifeBuilder;
      if (type.equalsIgnoreCase("n")) {
         lifeBuilder = createNpcLife(objectId, id);
      } else if (type.equalsIgnoreCase("m")) {
         lifeBuilder = createMonsterLife(objectId, id, mobTime, team);
      } else {
         System.out.println("Unknown life type " + type);
         return Optional.empty();
      }
      lifeBuilder.setCy(cy);
      lifeBuilder.setF(f);
      lifeBuilder.setFh(fh);
      lifeBuilder.setRx0(rx0);
      lifeBuilder.setRx1(rx1);
      lifeBuilder.setPosition(new Point(x, y));
      lifeBuilder.setHide(hide == 1);

      return Optional.of(lifeBuilder.build());
   }

   protected static NpcBuilder createNpcLife(int objectId, int id) {
      return new NpcBuilder(objectId, id)
            .setName(MapleDataTool.getString(id + "/name", DataRegistry.getInstance().getNpcNameData())
                  .orElse("MISSINGNO"));
   }

   protected static MonsterBuilder createMonsterLife(int objectId, int id, int mobTime, int team) {
      return new MonsterBuilder(objectId, id)
            .setMobTime(mobTime)
            .setTeam(team);
   }
}
