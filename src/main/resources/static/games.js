$(function() {

    var url = "api/games";

    console.log( "ready!" );

    $.getJSON( url, function( games ) {

        var gamesList = [];

        $.each( games, function( key, game ) {
        console.log("Ahi va");
        console.log(game);

            gamesList.push( "<li id='" + game.id + "'>" + new Date(game.created).toLocaleString());

            var gamePlayerList = game.GamePlayers;

            $.each( gamePlayerList, function(key, player) {
                var player = gamePlayerList[key].player;

                gamesList.push(", " + player.userName);
            });

            gamesList.push("</li>");

        });

        $("#output").append(gamesList.join( "" ));

    });
});
