$(function() {

    var url = "api/games";

    console.log( "ready!" );

    $.getJSON( url, function( games ) {

        var gamesList = [];

        $.each( games, function( key, game ) {
        console.log("Ahi va");
        console.log(game);
            gamesList.push( "<li id='" + game.id + "'>" + game.created + "</li>" );
        });

        $("#output").append(gamesList.join( "" ));

    });
});
