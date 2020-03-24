const http = require("http");
const https = require("https");
const express = require("express");
const url = require("url");
const cmd = require("child_process");
const bodyParser = require("body-parser");

const HTTP_PORT = 10803;
//const HTTPS_PORT = 10804;

//bash one-liners for getting/setting volume
//you may need to change these based on the sytem that you are running, soundcards installed etc.
const GET_VOLUME = "pactl list sinks | grep '^[[:space:]]Volume:' | head -n $(( $SINK + 2 )) | tail -n 1 | sed -e 's,.* \([0-9][0-9]*\)%.*,\1,'"
const SET_VOLUME = "pactl set-sink-volume 1 $1%";	//NOTE this guy takes a variable that replaces "$1"

//basic rhythmbox commands
const PLAY = "rhythmbox-client --play";
const PAUSE = "rhythmbox-client --pause";
const STOP = "rhythmbox-client --stop";
//const CURRENT_SONG = "rhythmbox-client  --print-playing-format=\"{ 'artist':'%ta', 'album':'%at', 'track':'%tt', 'duration':'%td', 'elapsed':'%te'}\"";
CURRENT_SONG = "rhythmbox-client  --print-playing-format=\"%ta\n%at\n%tt\n%td\n%te\n\""
const NEXT = "rhythmbox-client --next";
const PREVIOUS = "rhythmbox-client --previous";

var app = express();
app.disable("x-powered-by");
app.enable("trust proxy");
app.use(bodyParser.json());

var httpServer = http.createServer(app);

var port = HTTP_PORT;

httpServer.listen(port);

app.get("/rhythmbox/ping", function(req, res){
	res.sendStatus(200);
	//TODO maybe send "uname -n" output instead so they can be identified if multiple also ifconfig ip
});

app.get("/volume/get", function(req, res){
	/*vol.get().then(level => {
	    	console.log(level);
		res.send(JSON.stringify(
			{
				"volume": level*100
			}
		));
	    	//=> 0.45
	});*/
	/*vol.get(function(err, level){
		if(err){
			console.error(err);
			res.send(500);
		}else{
			console.log("volume: "+level);
			res.send(JSON.stringify(
			{
				"volume": level*100
			}
		));
		}
	});*/
	cmd.exec(GET_VOLUME, (error, stdout, stderr) => {
		//console.log(stdout);
		//console.log(stderr);
		//GET_VOLUME doesnt seem to run the sed code.., equivalent below
		let vol = 1*stdout.match(/[0-9]+%/)[0].replace("%", "");
		//console.log(vol);
		res.send(JSON.stringify({
			"volume": vol
		}));
	});
	//console.log(vol);
	
});

app.get("/volume/set/:value", function(req, res){
	let value = req.params.value;
	if(!isNaN(value)){
		//snap value to range
		value = value > 100 ? 100 : value;
		value < 0 ? 0 : value;

		let setVolCmd = SET_VOLUME.replace("$1", value);
		cmd.exec(setVolCmd);
		res.sendStatus(200);
	}else{//otherwise ignore, bad or malicious input
		res.sendStatus(400);
	}
});

app.get("/rhythmbox/play", function(req, res){
	cmd.exec(PLAY);
	res.sendStatus(200);
});

app.get("/rhythmbox/pause", function(req, res){
	cmd.exec(PAUSE);
	res.sendStatus(200);
});

app.get("/rhythmbox/stop", function(req, res){
	cmd.exec(STOP);
	res.sendStatus(200);
});

app.get("/rhythmbox/next", function(req, res){
	cmd.exec(NEXT);
	res.sendStatus(200);
});

app.get("/rhythmbox/previous", function(req, res){
	cmd.exec(PREVIOUS);
	res.sendStatus(200);
});

app.get("/rhythmbox/current-song", function(req, res){
	cmd.exec(CURRENT_SONG, (error, stdout, stderr) => {
		try{
			//console.log(stdout);
			let vals = stdout.split("\n");
			res.send(JSON.stringify({
				artist:vals[0],
				album:vals[1],
				track:vals[2],
				duration:vals[3],
				elapsed:vals[4]
			}));
		}catch(e){
			//uh oh
			console.log(e);
			res.sendStatus(500);
		}
	});
		
});

app.get("rhythmbox/search/:query", function(req, res){
	//TODO search 
	//return 200 and a list of songs (artist, album, track, duration) if possible
	//or return http code 404
	res.sendStatus(500);
});

app.get("rhythmbox/play/:song", function(req, res){
	//play song from query, if it exists
	res,sendStatus(500);
});




