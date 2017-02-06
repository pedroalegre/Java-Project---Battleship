var columnHeader = ["", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
var rowHeader = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
var hitsBoardHeader = ["Turn", "Hits on you", "Hits on opponent"];
var hitsBoardHeader2 = ["Hits", "Left", "Hits", "Left"];

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

var salvoes = [];

$(document).ready(function ($) {
	drawGrid();

	$("#addShipsButton").show();
	$("#placeSalvoesButton").hide();
	$("#confirmButton").hide();
	$("#cancelShipsButton").hide();
	$("#positionButton").hide();
	$("#fireSalvoesButton").hide();
	$("#cancelSalvoesButton").hide();
	$(".salvoesGrid").hide();

	$("#leaderboard").on("click", function() {
		window.location.replace("games.html");
	});

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

	$("#addShipsButton").on("click", function(event) {
		event.preventDefault();
		$(".salvoesGrid").hide();
		$("#addShipsButton").hide();
		$("#confirmButton").show();
		$("#cancelShipsButton").show();
		$("#positionButton").show();
		addShips();
	})
	// Add ships to the repository and grid
	$("#confirmButton").on("click", function(event) {
		event.preventDefault();

		if(validateShipsPlaced()) {
			paintShips();
			window.location.reload();
		} else {
			alert("Please place all ships");
		}
	})

	$("#cancelShipsButton").on("click", function(event) {
		event.preventDefault();
		ships = [{
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
		paintShips();
		$(".salvoesGrid").show();
		$("#addShipsButton").show();
		$(".placeShips").hide();
		$(".shipsGrid .cell").removeClass("hasShip");
	})

	$("#placeSalvoesButton").on("click", function(event) {
		event.preventDefault();
		$("#placeSalvoesButton").hide();
		$("#fireSalvoesButton").show();
		$("#cancelSalvoesButton").show();
		placeSalvo();
	})

	$("#fireSalvoesButton").on("click", function(event) {
		event.preventDefault();
		$("#addShipsButton").hide();
		$("#placeSalvoesButton").show();
		paintSalvoes();
	})

	$("#cancelSalvoesButton").on("click", function(event) {
		event.preventDefault();
		removeSalvo();
		salvoes = [];
		$(".salvoesGrid .cell").off();
		$("#fireSalvoesButton").hide();
		$("#cancelSalvoesButton").hide();
		$("#placeSalvoesButton").show();
	})
});

// draw the grids
function drawGrid() {

    createHeader(columnHeader);
    createRow();
    //createHitsHeader(hitsBoardHeader);
    //createHitsHeader(hitsBoardHeader2);

    drawShips(getGamePlayerPath(location.search));
    //getPlayerHits(getGamePlayerPath(location.search));
}

function createHeader(header) {
    var row = $("<tr class='headerRow'></tr>");
    $.each(header, function(i) {
        if(i==0) {
                row.append("<th class='columnHeader'></th>")
            } else {
                row.append("<th class='columnHeader'>" + header[i] + "</th>");
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

function createHitsHeader(header) {
	var row = $("<tr class='headerRow'></tr>");
	$.each(header, function(i) {
		if(header.length == 3) {
			if(i == 0) {
				row.append("<th class='hitsColumnHeader' rowspan='2'>" + header[i] + "</th>");
			} else {
				row.append("<th class='hitsColumnHeader' colspan='2'>" + header[i] + "</th>");
			}
		} else {
			row.append("<th class='hitsColumnHeader'>" + header[i] + "</th>");
		}

	});

	$(".hitsBoardHeader").append(row);
}

// Draw the players, ships and salvoes
function drawShips (gamePlayerValue) {
	var url = "api/game_view/" + gamePlayerValue.gp;
	var playerId = 0;
	$.getJSON( url, function(data) {
		var gameStatus = data.gameStatus;

		drawGameStatus(gameStatus, data);
		playersTitle();
		console.log(data.ships[0].locations);
		if(data.ships[0].locations.length > 0) {
			$("#addShipsButton").hide();
			$(".salvoesGrid").show();
			if(gameStatus == 0) {
				$("#placeSalvoesButton").show();
			} else if(gameStatus == 1) {
				$("#placeSalvoesButton").hide();
			}
			//$("#placeSalvoesButton").show();
			$(".shipsGrid .columnHeader").addClass("columnHeaderSmall").css("font-size", 10);
			$(".shipsGrid .rowHeader").addClass("rowHeaderSmall").css("font-size", 10);
			$(".shipsGrid .cell").addClass("cellSmall");
			$(".shipsGrid").removeClass("shipsGridStart");
			$(".shipsGrid .scan").remove();
		} else {
			$("#addShipsButton").show();
			$("#placeSalvoesButton").hide();
		}

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
				var hits = salvoLocation[cell].hits;

				$.each(salvoLocation2, function(cell) {
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

				// paint the hits on opponent ships
				$.each(hits, function(cell) {
					var cellLocation = $(".salvoesGrid").find("#" + hits[cell]);
					if(player == playerId) {
						cellLocation.addClass("hasHit");
					}
				})
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

// Put the ship types in the place ships container
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

// Paint the dropped ship on the grid
function drop(event) {
	event.preventDefault();
	var shipType = event.dataTransfer.getData("shipType");
	var shipLength;
	var cellId = event.target.id;
	var letter = cellId.substr(0,1);
	var number = Number(cellId.substr(1,2));
	var shipTemp = [];
	var letters;
	var paintShip = "";

	$.each(ships, function(i) {
		if(ships[i].shipType == shipType) {
			shipLength = ships[i].length;
		}
	});

	// Horizontal ships
	if($('input[name=position]:checked').val() == "horizontal") {
		paintShip = "no";
		horizontalShip(number, letter, shipLength, shipTemp, shipType);

	// Vertical ships
	} else {
		paintShip = "no";
		verticalShip(number, letter, shipLength, shipTemp, shipType);
	}
}

// Horizontal ships
function horizontalShip(number, letter, shipLength, shipTemp, shipType) {
	for(i = 0; i < shipLength; i++) {
		if(((number - 1 + shipLength) > 10) || ($("#" + (letter + (number + i))).hasClass("hasShip"))) {
			paintShip = "no";
		} else {
			shipTemp.push(letter + (number + i));
			paintShip = "yes";
		}
	}

	if(paintShip == "yes") {
		for(i = 0; i < shipLength; i++) {
			$("#" + (letter + (number + i))).addClass("hasShip");
			$("#" + shipType).removeAttr("draggable").removeClass("shipBlock").addClass("shipPlaced");
		}
	}
	// Put the ship locations in the ships list
	addShipLocations(shipType, shipTemp, shipLength);
}

// Vertical ships
function verticalShip(number, letter, shipLength, shipTemp, shipType) {
	letters = rowHeader.indexOf(letter);

	for(i = 0; i < shipLength; i++) {
		if(letters + shipLength > 10) {
			paintShip = "no";
			break;
		} else if($("#" + (rowHeader[letters + i] + number)).hasClass("hasShip")) {
			paintShip = "no";
			break;
		} else {
			shipTemp.push(rowHeader[letters + i] + number);
			paintShip = "yes";
		}
	}

	if(paintShip == "yes") {
		for(i = 0; i < shipLength; i++) {
			$("#" + (rowHeader[letters + i] + number)).addClass("hasShip");
			$("#" + shipType).removeAttr("draggable").removeClass("shipBlock").addClass("shipPlaced");
		}
	}
	// Put the ship locations in the ships list
	addShipLocations(shipType, shipTemp, shipLength);
}

// Put the ship locations in the ships list
function addShipLocations(shipType, shipTemp, shipLength) {
	$.each(ships, function(i) {
		if(ships[i].shipType == shipType) {
			for(j = 0; j < shipLength; j++) {
				(ships[i].shipLocations).push(shipTemp[j]);
			}
		}
	});
}

// validate if all ships are placed (returns boolean)
function validateShipsPlaced() {
	return !($(".shipsList li").hasClass("shipBlock"));
}

// Paint the ships in the grid
function paintShips() {
	$.post( {
		url: "/api/games/players/" + getGamePlayerPath(location.search).gp + "/ships",
		data: JSON.stringify(ships),
		contentType: "application/json",

	}).done(function(data, textStatus, jqXHR) {
		$(".shipsGrid").hide().show();
		$(".salvoesGrid").show();
		$(".placeShips").hide();
		$(".shipsList").hide();
		$("#placeSalvoesButton").show();
		//window.location.reload();

	}).fail(function(jqXHR, textStatus, errorThrown) {
		window.location.reload();
	});
}

function paintSalvoes() {
	$.post( {
		url: "/api/games/players/" + getGamePlayerPath(location.search).gp + "/salvoes",
		contentType: "application/json",
		data: JSON.stringify({"salvoLocations": salvoes})

	}).done(function(data, textStatus, jqXHR) {
		console.log("success");
		window.location.reload();

	}).fail(function(jqXHR, textStatus, errorThrown) {
		console.log(jqXHR);
		console.log("SHIT!");
	});
}

function placeSalvo() {
	$(".salvoesGrid .cell").on("click", function(event) {
		if(salvoes.length == 3) {
			alert("You can't fire more than 3 shots per turn");
			$(".salvoesGrid .cell").off();
		} else if ($(".salvoesGrid #" + event.target.id).hasClass("hasSalvo")) {
			alert("You've already fired at this location");
		} else {
			salvoes.push(event.target.id);
			$(".salvoesGrid #" + event.target.id).addClass("hasSalvo");
		}
	})

}

function removeSalvo() {
	for(i = 0; i < salvoes.length; i++) {
		$("#" + salvoes[i]).removeClass("hasSalvo");
		window.location.reload();
	}

}

function getPlayerHits(gamePlayerValue) {
	var url = "/api/game_view/" + gamePlayerValue.gp;
	var turn = [
	{"turn": 1,
		"player": 1,
        		"hits": "H1",
        		"left": "H2"
        	}
	]	 ;
	turn.push({"turn": 1, "player": 2, "hits": ["A1", "A2"], "left": "A3"});

	$.getJSON( url, function( game ) {
	console.log(game);
		var gPlayer = 0;
		$.each(game.GamePlayers, function(key, gp) {
			if(gamePlayerValue.gp == game.GamePlayers[key].id) {
				gPlayer = game.GamePlayers[key].player.id;
				console.log(gPlayer);
			}

		});
		var row = [];
		$.each(game.salvoes, function(key, salvo) {
		console.log(salvo);
		row = $("<tr class='rows' id='" + salvo[key].turn + "'></tr>");
			$.each(salvo, function(key2, pSalvo) {
				console.log(gPlayer);
				console.log(pSalvo.player);
				if(gPlayer != pSalvo.player) {
					row.append("<td>" + pSalvo.turn + "</td>");
					row.append("<td>" + pSalvo.hits + "</td>");
					row.append("<td>" + 2 +  "</td>");
				} else {
					row.append("<td>" + pSalvo.hits + "</td>");
					row.append("<td>" + 1 +  "</td>");
				}
				console.log(row);
				$(".hitsBoardBody").append(row);

			})
		})

	});
}

function checkGameStatus(gamePlayerValue) {
	var gpId = gamePlayerValue.gp;

	setInterval(function() {
		$.get({
			url: "/api/game_view/" + gpId,

		}).done(function(data) {
		console.log(data.gameStatus);
			if(data.gameStatus != 1) {
				window.location.reload();
			}
		})
	}, 500);
}

function drawGameStatus(gameStatus, data) {
	switch(gameStatus) {
		// Can fire salvoes
		case 0:
			gameStatusCase0(data);
			break;

		// Wait for other player or salvoes
		case 1:
			checkGameStatus(getGamePlayerPath(location.search));
			gameStatusCase1(data);
			break;

		// Game over
		case 2:
			gameStatusCase2(data);
	}
}

function gameStatusCase0(data) {
	$("#placeSalvoesButton").show();
}

function gameStatusCase1(data) {
	alert("Wait for the other player");
	$("#placeSalvoesButton").hide();
}

function gameStatusCase2(data) {
	var gpId = gamePlayerValue.gp;

	$.post({
		url: "/api/" + gpId + "/winner",

	}).done(function (data, textStatus, jqXHR){
		if(jqXHR.responseJSON.winner) {
			alert("You win!");
		} else {
			alert("You lose!");
		}

	})
}