<?php

	// Connection info variables
	$host = "localhost";
	$username = "root";
	$password = "";
	$dbname = "robots";

	// Choosing UTF-8 as the default character encryption for the database
	$options = array(PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES utf8');

	// Connecting to the DB

	try {
		$db = new PDO("mysql:host={$host}; dbname={$dbname}; charset=utf8", $username, $password, $options);
	} catch(PDOException $ex) {
		die("Failed to connect to the database: " . $ex->getMessage());
	}
	// If PDO founds an error will thrown an error
	$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

	if(function_exists('get_magic_quotes_gpc') && get_magic_quotes_gpc()) {

		function undo_magic_quotes_gpc(&$array) {
			foreach ($array as &$value) {
				
				if(is_array($value)) {
					undo_magic_quotes_gpc($value);
				} else {
					$value = stripcslashes($value);
				}

			}
		}
		
		undo_magic_quotes_gpc($_POST);
		undo_magic_quotes_gpc($_GET);
		undo_magic_quotes_gpc($_COOKIE);
	}

	// Header to tell the browser that the content is encoded with utf-8
	header('Content-Type: text/html; charset=utf-8');

	session_start();
?>