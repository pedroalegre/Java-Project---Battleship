$(function() {

    var url = "api/games";

    console.log( "ready!" );

    $.getJSON( url, function( games ) {

        var gamesList = [];

        $.each( games, function( key, game ) {
        console.log("Ahi va");
        console.log(game);
            var gameDate = new Date(game.created);
            console.log(gameDate);
            gamesList.push( "<li id='" + game.id + "'>" + gameDate + "</li>" );
        });

        $("#output").append(gamesList.join( "" ));

    });
});
