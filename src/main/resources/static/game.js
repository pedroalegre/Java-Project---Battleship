var columnHeader = ["", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
var rowHeader = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

drawGrid();

function drawGrid() {

    createHeader();
    createRow();

    drawShips(getGamePlayerPath(location.search));
}

function createHeader() {
    var row = $("<tr class='headerRow'></tr>");
    $.each(columnHeader, function(i) {
        if(i==0) {
                row.append("<th class='columnHeader'></th>")
            } else {
                row.append("<th class='columnHeader'>" + columnHeader[i] + "</th>");
            }
    });

    $(".header").append(row);
}

function createRow() {
    $.each(rowHeader, function(i) {
        var row = $("<tr class='row'></tr>");
        $.each(columnHeader, function(j) {
            if(j==0) {
                row.append("<td class='rowHeader'>" + rowHeader[i] + "</td>");
            } else {
                row.append("<td class='cell' id='" + rowHeader[i] + columnHeader[j] + "'></td>");
            }
        });

        $(".rows").append(row);
    });
}

function drawShips (gamePlayerValue) {
    var url = "api/game_view/" + gamePlayerValue.gp;

    $.getJSON( url, function(data) {
        playersTitle();

        //show the game and players info
        $.each( data.GamePlayers, function(player) {
            var playerName = data.GamePlayers[player].player.userName;
            if(gamePlayerValue.gp == data.GamePlayers[player].id) {
                $("#player1").append(playerName + " (You)");
                $("#vs").append(" VS ");
            } else {
                $("#player2").append(playerName);
            }
        });

        // draw the ships
        $.each( data.ships, function(ship) {
            var shipLocation = data.ships[ship].locations;
            $.each( shipLocation, function(cell) {
                $("#" + shipLocation[cell]).addClass("hasShip");
            });
        });

		// draw the salvoes
        $.each(data.salvoes, function(salvo) {
        	var salvoLocation = data.salvoes[salvo];
        	console.log(salvoLocation);
        	$.each( salvoLocation, function(cell) {
        		var salvoLocation2 = salvoLocation[cell].locations;
        		var turn = salvoLocation[cell].turn;
        		var player = salvoLocation[cell].player;

				$.each( salvoLocation2, function(cell) {
					var cellLocation = $(".shipsGrid").find("#" + salvoLocation2[cell]);
					if(gamePlayerValue.gp == player) {
						$(".salvoesGrid").find("#" + salvoLocation2[cell]).addClass("hasSalvo").text(turn);
					} else {
						cellLocation.addClass("hasSalvo").text(turn);
						if(cellLocation.hasClass("hasShip") && cellLocation.hasClass("hasSalvo")) {
							cellLocation.addClass("hasHit");
						}
					}
				});
        	});
        });
    });
};

function getGamePlayerPath(path) {
    var gamePlayerValue = {};
    var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

    path.replace(reg, function(match, param, val) {
        gamePlayerValue[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
    });
    return gamePlayerValue;
}

function playersTitle() {
    $(".playerInfo").append("<div id=player1>");
    $(".playerInfo").append("<div id=vs>");
    $(".playerInfo").append("<div id=player2>");
}