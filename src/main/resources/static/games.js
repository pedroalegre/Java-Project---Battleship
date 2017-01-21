var leaderboardHeader = ["Name", "Total", "Won", "Lost", "Tied"];

$(document).ready(function ($) {
	$("#logoutform").hide();
	$("#loginform").submit(function (event) {
		var loginMessage = "Welcome back, Admiral."
		loginUser(loginMessage);
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
			$("#gameslist").hide();
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
			var signupMessage = "Sign up successful. Welcome to Battleship, Admiral.";
			loginUser(signupMessage);
			$("#loginform").hide();
			$("#logoutform").show();
			$("#leaderboard").show();
			$("#signupform").hide();

		}).fail(function(jqXHR, textStatus, errorThrown) {
			alert("Stop hacking and put the right credentials!!");
		});
	});

	$("#createGame").submit(function (event) {
		event.preventDefault();
		$.ajax({
			timeout: 1000,
			type: 'POST',
			url: '/api/games'

		}).done(function(data, textStatus, jqXHR) {
			var gpid = jqXHR.responseJSON.gpid;
			window.location.replace("game.html?gp=" + gpid);

		}).fail(function(jqXHR, textStatus, errorThrown) {
			alert("Error. You have to be logged in to create a new game");
		});
	});
});

function getPlayerScores() {
    var url = "api/games";
    var names = [];
    var scores = {};
    var won = {};
    var lost = {};
    var tied = {};

    console.log( "ready!" );

	// create the list of games
    $.getJSON( url, function( games ) {

		var gamesList = [];

		$.each( games.games, function( key, game ) {
		console.log("Ahi va");

			var url;
			var gameId;
			gamesList = [];

			var gamePlayerList = game.players;
			gamesList.push( "<li id='" + game.id + "'>" + new Date(game.created).toLocaleString());

			$.each( gamePlayerList, function(key2) {
				var player = gamePlayerList[key2];
				gamesList.push(", " + player.name);

				if(gamePlayerList.length == 1) {
					var joinButton = $('<button/>', {
						text: 'Join game',
						id: 'data-' + game.id,
						class: 'btn btn-primary',
						click: function() {
							var url2 = 'api/game/' + game.id + '/players';
							$.ajax({
								timeout: 1000,
								type: 'POST',
								url: url2

							}).done(function(data, textStatus, jqXHR) {
								var gpDestination = jqXHR.responseJSON.gpid;
								url = "game.html?gp=" + gpDestination;

								window.location.replace(url);

							}).fail(function(jqXHR, textStatus, errorThrown) {
							console.log(url2);
								alert('Error!');
							});
						}
					});
				}

				if(games.player.id == game.players[key2].id) {
					url = "game.html?gp=" + game.players[key2].gpid;
				} else {
					$("#gameslist").append(joinButton);
				}
			});

			gamesList.push("</li>");

			if(url != undefined) {
				var playButton = $('<button/>', {
					text: 'Play game',
					id: 'data-p' + game.id,
					class: 'btn btn-warning',
					click: function() {
						window.location.replace(url);
					}
				});

				$("#gameslist").append(gamesList.join( "" ));
				$("#gameslist").append(playButton);
			} else {
				if(gamePlayerList.length == 2) {
					$("#gameslist").append(gamesList.join( "" ));
				} else {
					$("#data-" + game.id).before(gamesList.join(""));
				}
			}
		});

		var playerList = [];
		games = games.games;

		//get the scores for the leaderboard
		$.each( games, function(key) {
			$.each(games[key].players, function(key2) {
				var score = games[key].players[key2].score;
				var name = games[key].players[key2].name;

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

// login a user
function loginUser(welcomeMessage) {
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
		alert(welcomeMessage);
		$("#loginform").hide();
		$("#logoutform").show();
		$("#leaderboard").show();
		$("#signupform").hide();

	})
	.fail(function(jqXHR, textStatus, errorThrown) {
		alert("Stop hacking and put the right credentials!!");
	});
}