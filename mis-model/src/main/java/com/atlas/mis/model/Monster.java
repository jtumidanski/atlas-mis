package com.atlas.mis.model;

public record Monster(int objectId, int id, int mobTime, int team, int cy, int f, int fh, int rx0, int rx1, int x, int y,
                      boolean hide) implements Life {
}
