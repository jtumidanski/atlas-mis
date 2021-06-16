package _map

type DataContainer struct {
	Data DataBody `json:"data"`
}

type DataBody struct {
	Id         string     `json:"id"`
	Type       string     `json:"type"`
	Attributes Attributes `json:"attributes"`
}

type Attributes struct {
	Name              string                     `json:"name"`
	StreetName        string                     `json:"street_name"`
	ReturnMapId       uint32                     `json:"return_map_id"`
	MonsterRate       float64                    `json:"monster_rate"`
	OnFirstUserEnter  string                     `json:"on_first_user_enter"`
	OnUserEnter       string                     `json:"on_user_enter"`
	FieldLimit        uint32                     `json:"field_limit"`
	MobInterval       uint32                     `json:"mob_interval"`
	Seats             uint32                     `json:"seats"`
	Clock             bool                       `json:"clock"`
	EverLast          bool                       `json:"ever_last"`
	Town              bool                       `json:"town"`
	DecHP             uint32                     `json:"dec_hp"`
	ProtectItem       uint32                     `json:"protect_item"`
	ForcedReturnMapId uint32                     `json:"forced_return_map_id"`
	Boat              bool                       `json:"boat"`
	TimeLimit         int32                      `json:"time_limit"`
	FieldType         uint32                     `json:"field_type"`
	MobCapacity       uint32                     `json:"mob_capacity"`
	Recovery          float64                    `json:"recovery"`
	MapArea           rectangleAttributes        `json:"map_area"`
	Areas             []rectangleAttributes      `json:"areas"`
	BackgroundTypes   []backgroundTypeAttributes `json:"background_types"`
}

type rectangleAttributes struct {
	X      int16  `json:"x"`
	Y      int16  `json:"y"`
	Width  int16 `json:"width"`
	Height int16 `json:"height"`
}

type backgroundTypeAttributes struct {
	LayerNumber    uint32 `json:"layer_number"`
	BackgroundType uint32 `json:"background_type"`
}