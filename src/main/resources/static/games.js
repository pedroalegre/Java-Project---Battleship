var leaderboardHeader = ["Name", "Total", "Won", "Lost", "Tied"];

function getPlayerScores() {
    var url = "api/games";
    var names = [];
    var scores = {};
    var won = {};
    var lost = {};
    var tied = {};

    console.log( "ready!" );

    $.getJSON( url, function( games ) {

		var playerList = [];
		games = games.games;

		//get the scores for the leaderboard
		$.each( games, function(key) {
			$.each(games[key].GamePlayers, function(key2) {
				var score = games[key].GamePlayers[key2].score;
				var name = games[key].GamePlayers[key2].player.userName;

				if($.inArray(name, names) == -1) {
					names.push(name);

					if(score != undefined) {
						scores[name] = score;
					}
				} else {
					if(score != undefined) {
						scores[name] += score;
					}
				}

				// get the totals
				switch(score) {
					case 0:
						if(lost[name]) {
							lost[name] += 1;
						} else {
							lost[name] = 1;
						}
						break;
					case 0.5:
						if(tied[name]) {
							tied[name] += 1;
						} else {
							tied[name] = 1;
						}
						break;
					case 1:
						if(won[name]) {
							won[name] += 1;
						} else {
							won[name] = 1;
						}
				}
			});
		});

		$(".leaderboardBody").empty();

		// replace the undefined scores with 0
        $.each(names, function(i) {
        	if(scores[names[i]] == undefined) {
        		scores[names[i]] = 0;
        	}
			if(won[names[i]] == undefined) {
        		won[names[i]] = 0;
        	}

        	if(lost[names[i]] == undefined) {
				lost[names[i]] = 0;
			}
			if(tied[names[i]] == undefined) {
				tied[names[i]] = 0;
			}

			// put the scores on the leaderboard
			var row = $("<tr class='rows'></tr>");
			row.append("<td>" + names[i] + "</td>");
			row.append("<td>" + scores[names[i]] + "</td>");
			row.append("<td>" + won[names[i]] + "</td>");
			row.append("<td>" + lost[names[i]] + "</td>");
			row.append("<td>" + tied[names[i]] + "</td>");
			$(".leaderboardBody").append(row);
		});

		/*
		games.forEach(function(game){
			gamePlayerList = game.GamePlayers;

			$.each( gamePlayerList, function(key, gamePlayer) {


				//console.log(scores);
				playerId = gamePlayer.player.id;
				//playerList.push({ [playerId]: gamePlayer.player });
				score = gamePlayer.score;



				//playerList = $.grep(playerList, function(i) { return i.id == "2"; });
				playerList.push(gamePlayer.player);

				// create the list of unique usernames

				if( $.inArray(playerId, playerList) == -1 ) {
					console.log(playerId);
					console.log(playerList);

					if(score == undefined) {
						player.score = 0;
					} else {
						player.score = score;
					}


					playerList.push("<tr>");
					playerList.push("<td id='" + player.id + "'>" );
					playerList.push(player.userName);
					playerList.push("</td>");
					playerList.push("<td>" + player.score + "</td>");
					playerList.push("</tr>");
					// playerList.push("<li>" + player.score + "</li>");


				} else {
				player.score += score;
				}

			});
		})
		/*
		$.each( games, function( key, game ) {

		});
		*/
		//$(".leaderboardBody").append(playerList.join( "" ));
	});
};

function createHeader() {
	$(".leaderboardHeader").empty();
    var row = $("<tr class='headerRow'></tr>");
    $.each(leaderboardHeader, function(i) {
    	row.append("<th class='columnHeader'>" + leaderboardHeader[i] + "</th>");
    })
    $(".leaderboardHeader").append(row);
};

$(document).ready(function ($) {
	$("#logoutform").hide();
	$("#loginform").submit(function (event) {
		loginUser();
	});

	$("#logoutform").submit(function (event) {
		event.preventDefault();
		$.ajax({
			timeout: 1000,
			type: 'POST',
			url: '/api/logout'

		}).done(function(data, textStatus, jqXHR) {
			alert("Leaving so soon?");
			$("#loginform").show();
			$("#logoutform").hide();
			$("#leaderboard").hide();
			$("#signupform").show();
		});
	});

	$("#signupform").submit(function (event) {
		event.preventDefault();
		var data = "userName=" + $("#username").val() + "&password=" + $("#password").val();
		$.ajax({
			data: data,
			timeout: 1000,
			type: 'POST',
			url: '/api/players'

		}).done(function(data, textStatus, jqXHR) {
			loginUser();
			$("#loginform").hide();
			$("#logoutform").show();
			$("#leaderboard").show();
			$("#signupform").hide();

		}).fail(function(jqXHR, textStatus, errorThrown) {
			alert("Stop hacking and put the right credentials!!");
		});
	});
});

// login a user
function loginUser() {
	event.preventDefault();
	var data = "username=" + $("#username").val() + "&password=" + $("#password").val();
	$.ajax({
		data: data,
		timeout: 1000,
		type: 'POST',
		url: '/api/login'

	}).done(function(data, textStatus, jqXHR) {
		createHeader();
		getPlayerScores();
		alert("Welcome admiral!");
		$("#loginform").hide();
		$("#logoutform").show();
		$("#leaderboard").show();
		$("#signupform").hide();

	})
	.fail(function(jqXHR, textStatus, errorThrown) {
		alert("Stop hacking and put the right credentials!!");
	});
}