package rest

import (
	_map "atlas-mis/map"
	"atlas-mis/monster"
	"context"
	"github.com/gorilla/mux"
	"github.com/sirupsen/logrus"
	"net/http"
	"sync"
)

func CreateRestService(l *logrus.Logger, ctx context.Context, wg *sync.WaitGroup) {
	go NewServer(l, ctx, wg, ProduceRoutes())
}

func ProduceRoutes() func(l logrus.FieldLogger) http.Handler {
	return func(l logrus.FieldLogger) http.Handler {
		router := mux.NewRouter().PathPrefix("/ms/mis").Subrouter().StrictSlash(true)
		router.Use(CommonHeader)

		monr := router.PathPrefix("/monsters").Subrouter()
		monr.HandleFunc("/{monsterId}", monster.HandleGetMonsterRequest(l)).Methods(http.MethodGet)
		monr.HandleFunc("/{monsterId}/loseItems", monster.HandleGetMonsterLoseItemsRequest(l)).Methods(http.MethodGet)

		WithMap := _map.WithMap(l)
		WithMapPortal := _map.WithMapPortal(l)
		WithMapNPC := _map.WithMapNPC(l)
		mapr := router.PathPrefix("/maps").Subrouter()
		mapr.HandleFunc("/{mapId}", WithMap(_map.HandleGetMapRequest(l))).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/portals", WithMap(_map.HandleGetMapPortalsByNameRequest(l))).Queries("name", "{name}").Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/portals", WithMap(_map.HandleGetMapPortalsRequest(l))).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/portals/{portalId}", WithMapPortal(_map.HandleGetMapPortalRequest(l))).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/reactors", WithMap(_map.HandleGetMapReactorsRequest(l))).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/npcs", WithMap(_map.HandleGetMapNPCsByObjectIdRequest(l))).Queries("objectId", "{objectId}").Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/npcs", WithMap(_map.HandleGetMapNPCsRequest(l))).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/npcs/{npcId}", WithMapNPC(_map.HandleGetMapNPCRequest(l))).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/monsters", WithMap(_map.HandleGetMapMonstersRequest(l))).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/dropPosition", WithMap(_map.HandleGetMapDropPositionRequest(l))).Methods(http.MethodPost)

		return router
	}
}
