var http = require("http");
var url = require("url");

var Db = require('mongodb').Db,
MongoClient = require('mongodb').MongoClient,
Server = require('mongodb').Server,
ReplSetServers = require('mongodb').ReplSetServers,
ObjectID = require('mongodb').ObjectID,
Binary = require('mongodb').Binary,
GridStore = require('mongodb').GridStore,
Grid = require('mongodb').Grid,
Code = require('mongodb').Code,
assert = require('assert');


var crypto = require('crypto');
var express = require('express');
var bodyParser = require('body-parser');
var app = express();

app.use(bodyParser());
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
	extended: true
})); 


var db = new Db('UserInfo', new Server('127.0.0.1', 27017));
var PORT = 40001;

app.get('/', function(req, res)
{
	res.end("Start Port " + PORT);
});

// 자체 회원가입
app.post('/signUp', function(req, res)
{	
	var param = req.body;

	var result = new Array();
	if(param.id == "" || param.passwd == "" || param.name == "" || param.birth == "" || param.sex == "" || param.uuid == "" || param.model == "" ||
		param.id == null || param.passwd == null || param.name == null || param.birth == null || param.sex == null || param.uuid == null || param.model == null)
	{
		result = {
			code 	: -3,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}
	if(param.passwd.length < 8)
	{
		result = {
			code 	: -1,
			message : "More than 8 characters"
		}
		res.end(JSON.stringify(result));
		return;
	}
	db.open(function(err, db)
	{
		assert.equal(null, err);
		db.createCollection('userinfo', function(err, collection)
		{
			assert.equal(null, err);
			collection.findOne({id:param.id, api:'basic'},function(err, doc)
			{
				assert.equal(null, err);
				if(doc == null)
				{
					collection.find().toArray(function(err,docs)
					{
						var sessionKey;
						while(true)
						{
							var bFlag = false;
							sessionKey = randomValueHex(60);
							for( var v in docs )
							{
								if(docs['session'] == sessionKey)
								{
									bFlag = true;
									break;
								}
							}
							if(!bFlag)
								break;
						}
						collection.insert(
						{
							id 		: param.id,
							session : sessionKey,
							passwd 	: param.passwd,
							name	: param.name,
							birth 	: param.birth,
							sex		: param.sex,
							uuid 	: param.uuid,
							model 	: param.model,
							api		: 'basic'
						},function(err,doc)
						{
							assert.equal(null, err);
							result = {
								code 	: 1,
								message : "Success signup",
								session : sessionKey
							}
							res.end(JSON.stringify(result));
							db.close();
						}); // insert
					});
				}
				else
				{
					result = {
						code 	: -2,
						message : "ID is duplicated"
					}
					res.end(JSON.stringify(result));
					db.close();
				}

			}); // collection.findOne
		}); // db.createCollection
	}); // db.open
});

// 페이스북으로 회원가입
app.post('/signFacebook', function(req, res)
{	
	var param = req.body;

	var result = new Array();
	if(param.id == "" || param.name == "" || param.sex == "" || param.uuid == "" || param.model == "" ||
		param.id == null || param.name == null || param.sex == null || param.uuid == null || param.model == null)
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}
	db.open(function(err, db)
	{
		assert.equal(null, err);
		db.createCollection('userinfo', function(err, collection)
		{
			assert.equal(null, err);
			collection.findOne({id:param.id, api:'facebook'},function(err, doc)
			{
				assert.equal(null, err);
				if(doc == null) // Facebook으로 회원가입
				{
					collection.find().toArray(function(err, docs)
					{
						assert.equal(null, err);
						var sessionKey;
						while(true)
						{
							var bFlag = false;
							sessionKey = randomValueHex(60);
							for( var v in docs )
							{
								if(docs['session'] == sessionKey)
								{
									bFlag = true;
									break;
								}
							}
							if(!bFlag)
								break;
						}
						collection.insert(
						{
							id 		: param.id,
							session : sessionKey,
							passwd 	: '',
							name	: param.name,
							birth 	: param.birth,
							sex		: param.sex,
							uuid 	: param.uuid,
							model 	: param.model,
							api		: 'facebook'
						},function(err,doc)
						{
							assert.equal(null, err);
							result = {
								code 	: 1,
								message : "Success facebook signup",
								session : sessionKey
							}
							res.end(JSON.stringify(result));
							db.close();
						}); // insert
					}); // find.toArray
				}
				else // 페이스북으로 로그인
				{
					result = {
						code 	: 2,
						message : "Success facebook login",
						session : doc['session']
					}
					res.end(JSON.stringify(result));
					db.close();
				}
			});

		}); // db.createCollection
	}); // db.opem
});

// 자체 아이디로 로그인
app.post('/login', function(req, res)
{	
	var param = req.body;

	var result = new Array();
	if(param.id == "" || param.passwd == "" ||
		param.id == null || param.passwd == null)
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}
	db.open(function(err, db)
	{
		assert.equal(null, err);
		db.createCollection('userinfo', function(err, collection)
		{
			assert.equal(null, err);
			collection.findOne({id:param.id, passwd:param.passwd, api:'basic'},function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					result = {
						code 	: 1,
						message : "Success Login",
						session : doc['session']
					}
					res.end(JSON.stringify(result));
					db.close();
				}
				else
				{
					result = {
						code 	: -2,
						message : "Uncorrect password or Undefined ID"
					}
					res.end(JSON.stringify(result));
					db.close();
				}
			});
		});
	});
});

// 세션으로 로그인
app.post('/validateSession', function(req, res)
{
	var param = req.body;

	var result = new Array();
	if(param.session == "" || param.session == null )
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}
	db.open(function(err, db)
	{
		assert.equal(null, err);
		db.createCollection('userinfo', function(err, collection)
		{
			assert.equal(null, err);
			collection.findOne({session:param.session},function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					result = {
						code 	: 1,
						message : "Validate Session"
					}
					res.end(JSON.stringify(result));
					db.close();
				}
				else
				{
					result = {
						code 	: -2,
						message : "Invalidate Session"
					}
					res.end(JSON.stringify(result));
					db.close();
				}
			});
		});
	});
});

// 세션으로 로그아웃
app.post('/logOut', function(req, res)
{
	var param = req.body;

	var result = new Array();

	if(param.session == "" || param.session == null)
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}

	db.open(function(err, db)
	{
		assert.equal(null, err);
		db.createCollection('userinfo', function(err, collection)
		{
			assert.equal(null, err);
			collection.findOne({session:param.session},function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					collection.find().toArray(function(err,docs)
					{
						var sessionKey;
						while(true)
						{
							var bFlag = false;
							sessionKey = randomValueHex(60);
							for( var v in docs )
							{
								if(docs['session'] == sessionKey)
								{
									bFlag = true;
									break;
								}
							}
							if(!bFlag)
								break;
						}
						collection.update( { session:doc['session']}, {$set: {session:sessionKey} } );

						result = {
							code 	: 1,
							message : "Success Logout"
						}
						res.end(JSON.stringify(result));
						db.close();
					});
				}
				else
				{
					result = {
						code 	: -2,
						message : "Invalidate Session"
					}
					res.end(JSON.stringify(result));
					db.close();
				}
			});
		});
	});
});



function randomValueHex (len)
{
	return crypto.randomBytes( Math.ceil( len / 2 ) )
	.toString('hex') // hexdecimal로 바꿈
	.slice(0,len); // len만큼 짜르
}

http.createServer(app).listen(PORT,function()
{
	console.log('Server Running ' + PORT);
});