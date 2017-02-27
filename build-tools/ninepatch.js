#!/usr/bin/env node

/**
Usage: ninepatch.sh (icon.png)
	icon.png: image to center, present on running directory
	(color): on Line 55, the background color used. 
	TODO add a 'color' parameter. It's janky.
	TODO sample image and figure out mathematically what is a good background color (heavy color theory)

 Convert a base icon into 9-patch assets
 Outputs base composites for each resolution and .9 files
*/
(function(specification, background){
	var output_dir = 'ninepatch-output/';
	var shell = require('child_process');
	// create directories if necessary
	var mkdirp = 'mkdir -p ' + output_dir;
	//console.log(mkdirp);
	shell.exec(mkdirp, out);
	for (var density in specification){
		specification[density].replace(/(\d+) (\d+)x(\d+)/, 
			function(spec, icon, width, height){
				['port', 'land'].forEach(function(orientation){
					var output = output_dir + 'display-'+orientation+'-'+density+'-splashscreen.9.png',
						content = (icon * 1.6),
						// coerce these strings into numbers, for maths
						w = orientation == 'port' ? +width : +height,
						h = orientation == 'port' ? +height: +width,
						// Assemble shell command (Imagemagick)
						convert = '/opt/local/bin/convert icon.png' +
					// 1 Take base icon, resize for each resolution
		                ' -gravity center -resize ' + content +
		                ' -background "' + background + '" -flatten' + 
					// 2 Extend background
		                ' -extent ' + w + 'x' + h +
					// 3 Add transparent border
						' -matte -bordercolor none -border 1' +
					// 4 Draw 9-patch scalable (top/left) markers
						' -fill black ' +
						// top
						'-draw "line 1,0 '+((w - content)/2)+',0" -draw "line '+((w + content + 2)/2)+',0 '+(w - 1)+',0"' +
						// left
						' -draw "line 0,1 0,'+((h - content)/2)+'" -draw "line 0,'+((h + content + 2)/2)+' 0,'+(h - 1)+'" ' +
					// 5 Draw 9-patch fill (right/bottom) markers
						// not used
					// 6 Execute
	 					output;
			            console.log(convert);
			            shell.exec(convert, out);
				});
			});
	}
})({
//  name: 'icon widthxheight'
	ldpi: '36 320x426',
	mdpi: '48 320x470',
	hdpi: '72 480x640',
	xhdpi:'96 720x960',
	xxhdpi:'144 720x960',
}, '#ffffff');


function out(error, stdout, stderr) {
    stdout && console.log(stdout);
    stderr && console.log(stderr);
    if (error)
        console.log(error);
}