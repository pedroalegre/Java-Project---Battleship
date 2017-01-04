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

        //show the game and players info
        $.each( data.GamePlayers, function(player) {
            var playerName = data.GamePlayers[player].player.userName;

            if(gamePlayerValue.gp == data.GamePlayers[player].id) {
                $(".playerInfo").append(playerName + " (You)");
            } else {
                $(".playerInfo").append(playerName);
            }
        });

        // draw the ships
        $.each( data.ships, function(ship) {
            var shipLocation = data.ships[ship].locations;
            $.each( shipLocation, function(cell) {
                $("#" + shipLocation[cell]).addClass("hasShip");
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