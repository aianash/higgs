'use strict'

var gulp = require('gulp');
var plugins = require('gulp-load-plugins')();
var del = require('del');
var beep = require('beepbeep');
var path = require('path');
var open = require('open');
var streamqueue = require('streamqueue');
var runSequence = require('run-sequence');
var merge = require('merge-stream');
var coffee = require('gulp-coffee');
var thrift = require('gulp-thrift');
var coffeelint = require('gulp-coffeelint');
var nodemon = require('gulp-nodemon');

/**
 * Parse arguments
 */
var args = require('yargs')
    .alias('r', 'release')
    .alias('b', 'build')
    .default('build', true)
    .default('release', false)
    .argv;

var build = args.build || args.release;
var release = args.release;
var targetDir = path.resolve(release ? 'dist' : 'build');


// global error handler
var errorHandler = function(error) {
  if (build) {
    throw error;
  } else {
    beep(2, 170);
    plugins.util.log(error);
  }
};

// clean target dir
gulp.task('clean', function(done) {
  del([targetDir], done);
});


// lint coffeescript
gulp.task('coffeelint', function() {
	gulp.src(['app.coffee', '**/*.coffee'], {cwd: 'app'})
			.pipe(coffeelint())
			.pipe(coffeelint.reporter())
			.on('error', errorHandler);
});


// compile coffeescript and copy them.
gulp.task('compile', function() {
  return gulp
    .src(['**/*.coffee'], { cwd: 'app' })
    .pipe(coffee({bare: false}))
    .pipe(gulp.dest(targetDir))
    .on('error', errorHandler);
});


gulp.task('thrift-compile', function() {
  return gulp
    .src(['**/*.thrift'], {cwd: 'app'})
    .pipe(thrift({gen: 'js:node'}))
    .pipe(gulp.dest(path.join(targetDir, 'lib')))
    .on('error', errorHandler);
});

// run the develope node server and watch over changes
gulp.task('develop', function() {
  nodemon({
    script: 'server.coffee',
    ext: 'coffee',
    watch: 'app',
    env: {
      'NODE_ENV': 'development',
      'APP_DIR': targetDir
    },
  }).on('change', ['coffeelint', 'compile'])
  .on('restart', function() {
    console.log('restarted');
  });

});

gulp.task('config', function() {
  gulp.src('app/configs/**/*json')
      .pipe(gulp.dest(targetDir + '/configs'))
      .on('error', errorHandler)
});

gulp.task('noop', function() {});

// our main sequence, with some conditional jobs
gulp.task('default', function(done){
	runSequence(
		'clean',
		[
			'coffeelint',
			'compile',
      'config',
      'thrift-compile'
		],
		// release ? 'noop' : 'develop',
    done);
});