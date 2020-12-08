package com.atlas.mis.model;

public record Npc(int objectId, int id, String name, int cy, int f, int fh, int rx0, int rx1, int x, int y, boolean hide) implements Life {
}
