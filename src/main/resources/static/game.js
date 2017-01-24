var columnHeader = ["", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
var rowHeader = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

var ships = [{
		"shipType": "Carrier",
		"length": 5,
		"shipLocations": []
	}, {
		"shipType": "Battleship",
		"length": 4,
		"shipLocations": []
	}, {
		"shipType": "Submarine",
		"length": 3,
		"shipLocations": []
	}, {
		"shipType": "Destroyer",
		"length": 3,
		"shipLocations": []
	}, {
		"shipType": "PatrolBoat",
		"length": 2,
		"shipLocations": []
}];

$(document).ready(function ($) {
	drawGrid();

	// get the cellId on mouseover
    /*$('.cell').mouseup(function() {
       console.log(this.id);
    });*/

	$("#logoutform").submit(function (event) {
		event.preventDefault();
		$.ajax({
			timeout: 1000,
			type: 'POST',
			url: '/api/logout'

		}).done(function(data, textStatus, jqXHR) {
			window.location.replace("/games.html");
			alert("Leaving so soon?");
		});
	});

	$("#confirmButton").hide();
	$("#cancelButton").hide();
	$("#positionButton").hide();

	$("#addShipsButton").on("click", function(event) {
		event.preventDefault();
		$(".salvoesGrid").hide();
		$("#addShipsButton").hide();
		$("#confirmButton").show();
		$("#cancelButton").show();
		$("#positionButton").show();
		addShips();
	})
	// Add ships to the repository and grid
	$("#confirmButton").on("click", function(event) {
		event.preventDefault();
		$.post( {
			url: "/api/games/players/" + getGamePlayerPath(location.search).gp + "/ships",
			data: JSON.stringify(ships),
			contentType: "application/json",

		}).done(function(data, textStatus, jqXHR) {
			$(".salvoesGrid").show();
			window.location.reload();

		}).fail(function(jqXHR, textStatus, errorThrown) {
			console.log("shit!");
			window.location.reload();
		});
	})

	$("#cancelButton").on("click", function(event) {
		event.preventDefault();
		$(".salvoesGrid").show();
		$("#addShipsButton").show();
		$(".shipsList").hide();
		$("#confirmButton").hide();
		$("#cancelButton").hide();
		$("#positionButton").hide();
	})
});

// draw the grids
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
                row.append("<td class='cell' ondrop='drop(event, this)' ondragover='allowDrop(event)' id='" + rowHeader[i] + columnHeader[j] + "'></td>");
            }
        });

        $(".rows").append(row);
    });
}

function drawShips (gamePlayerValue) {
    var url = "api/game_view/" + gamePlayerValue.gp;
	var playerId = 0;
    $.getJSON( url, function(data) {
        playersTitle();

        //show the game and players info
        $.each( data.GamePlayers, function(player) {
            var playerName = data.GamePlayers[player].player.userName;
            if(gamePlayerValue.gp == data.GamePlayers[player].id) {
                $("#player1").append(playerName + " (You)");
                $("#vs").append(" VS ");
                playerId = data.GamePlayers[player].player.id;
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

        	$.each( salvoLocation, function(cell) {
        		var salvoLocation2 = salvoLocation[cell].locations;
        		var turn = salvoLocation[cell].turn;
        		var player = salvoLocation[cell].player;

				$.each( salvoLocation2, function(cell) {
					var cellLocation = $(".shipsGrid").find("#" + salvoLocation2[cell]);
					if(player == playerId) {
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
	}).fail(function() {
		window.location.replace("games.html");
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

function addShips() {
	$(".shipsList").empty();
	var shipsList = $("<ul class=shipsList></ul>");
	$.each(ships, function(i) {
		shipsList.append("<li class=" + ships[i].shipType + ">" + ships[i].shipType + "</li>");
		shipsList.append("<li class=shipBlock draggable=true ondragstart='drag(event)' id=" + ships[i].shipType + "></li>");
		$(".placeShips").append(shipsList);
		$("#" + ships[i].shipType).css("width", 40 * ships[i].length + 3.6 * ships[i].length + "px");
	});
}

function allowDrop(event) {
	event.preventDefault();
}

function drag(event) {
	event.dataTransfer.setData("shipType", event.target.id);
}

// Paint the ships on the grid
function drop(event) {
	event.preventDefault();
	var shipType = event.dataTransfer.getData("shipType");
	var shipLength;
	var cellId = event.target.id;
	var letter = cellId.substr(0,1);
	var number = Number(cellId.substr(1,2));
	var shipTemp = [];
	var letters;

	$.each(ships, function(i) {
		if(ships[i].shipType == shipType) {
			shipLength = ships[i].length;
		}
	});

	for(i = 0; i < shipLength; i++) {

		// Horizontal ships
		if($('input[name=position]:checked').val() == "horizontal") {
			horizontalShip(number, letter, shipLength, shipTemp);

		// Vertical ships
		} else {
			verticalShip(number, letter, shipLength, shipTemp);
		}
	}
}

function horizontalShip(number, letter, shipLength, shipTemp) {
	if(number + shipLength > 10) {
	} else {
		shipTemp.push(letter + (number + i));
		$("#" + (letter + (number + i))).addClass("hasShip");
	}
}

function verticalShip(number, letter, shipLength, shipTemp) {
	letters = rowHeader.indexOf(letter);

	if(letters + shipLength > 10) {
	} else {
		shipTemp.push(rowHeader[letters + i] + number);
		$("#" + (rowHeader[letters + i] + number)).addClass("hasShip");
	}
}

function hasShip() {

}