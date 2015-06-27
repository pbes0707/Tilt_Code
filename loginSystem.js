var http = require("http");
var url = require("url");
var fs = require('fs');
var events = require('events');
var multer  = require('multer')
var crypto = require('crypto');
var express = require('express');
var bodyParser = require('body-parser');

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


var app = express();

app.use(bodyParser());
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
	extended: true
}));
app.use(multer({ dest: './temp'}))
events.EventEmitter.defaultMaxListeners = 500;


var db = new Db('TiltCode', new Server('127.0.0.1', 27017));
var PORT = 40001;
var CHARGE_POINT = [5000, 10000, 30000, 50000, 100000, 200000, 500000, 50, -50000];

app.get('/', function(req, res)
{
	res.end("LoginSystem Start Port " + PORT);
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
							coupon	: [],
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
			collection.findOne({id:param.id, api:'facebook'},{coupon : 0, api : 0, passwd : 0},function(err, doc)
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
							coupon	: [],
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
						info	: doc
					}
					res.end(JSON.stringify(result));
					db.close();
				}
			});

		}); // db.createCollection
	}); // db.opem
});

// 자체 아이디로 로그인 사용자 + 관리자
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
			collection.findOne({id:param.id, passwd:param.passwd, api:{$in:['basic','manage']}},{coupon : 0, api : 0, passwd : 0},function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					result = {
						code 	: 1,
						message : "Success Login",
						info	: doc
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

// 세션으로 유효성
app.get('/validateSession', function(req, res)
{
	var parsedUrl = url.parse(req.url, true);
	var queryAsObject = parsedUrl.query;
	var jsonData = JSON.parse(JSON.stringify(queryAsObject));


	var result = new Array();
	if(jsonData.session == "" || jsonData.session == null )
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
			collection.findOne({session:jsonData.session},function(err, doc)
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



// 관리자 자체 회원가입
app.post('/signUpManager', function(req, res)
{	
	var param = req.body;

	var result = new Array();

	if(param.id == "" || param.passwd == "" || param.name == "" || param.birth == "" || param.phone == "" || param.sex == "" || param.company == "" ||
		param.id == null || param.passwd == null || param.name == null || param.birth == null || param.phone == null || param.sex == null || param.company == null)
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}
	if(param.passwd.length < 8)
	{
		result = {
			code 	: -3,
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
			collection.findOne({id:param.id, api:{ $in :['manage','basic']}},function(err, doc)
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
							phone	: param.phone,
							sex 	: param.sex,
							company : param.company,
							point	: 0,
							api		: 'manage',
							coupon	: []
						},function(err,doc)
						{
							assert.equal(null, err);
							result = {
								code 	: 1,
								message : "Success signup Manager",
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

// 이름 변경
app.post('/changeName',function(req, res)
{
	var param = req.body;

	var result = new Array();

	if(param.session == "" ||  param.name == "" ||
		param.session == null || param.name == null)
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
			collection.findOne({session:param.session, api:'basic'},function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					collection.update( {session:param.session,api:'basic'}, {$set : {name : param.name} }, function(err, doc)
					{
						assert.equal(null, err);
						result = {
							code 	: 1,
							message : "Success change name"
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

// 관리자 & 사용자 비밀번호 변경
app.post('/changePasswd',function(req, res)
{
	var param = req.body;

	var result = new Array();

	if(param.session == "" ||  param.currP == "" || param.changeP == "" || 
		param.session == null || param.currP == null || param.changeP == null)
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
			collection.findOne({session:param.session, api:{ $in : ['basic','manage'] }},function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					if(doc['passwd'] == param.currP)
					{
						collection.update( {session:param.session, api:{ $in : ['basic','manage'] } }, {$set : {passwd : param.changeP} }, function(err, doc)
						{
							assert.equal(null, err);
							result = {
								code 	: 1,
								message : "Success change password"
							}
							res.end(JSON.stringify(result));
							db.close();
						});
					}
					else
					{
						result = {
							code 	: -3,
							message : "Uncorrect current password"
						}
						res.end(JSON.stringify(result));
						db.close();
					}
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

app.post('/pointCharge', function(req, res)
{
	var param = req.body;

	var result = new Array();

	if(param.session == "" || param.point == "" ||
		param.session == null || param.point == null)
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}
	if(param.point > 8 || param.point < 0)
	{
		result = {
			code 	: -2,
			message : "Do not point cracking"
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
			collection.findOne({session : param.session, api : 'manage'}, function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					collection.update({session : param.session, api : 'manage'}, { $inc : {point : CHARGE_POINT[param.point]} }, function(err, temp)
					{
						assert.equal(null, err);
						result = {
							code 	: 1,
							message : "Success charge point",
							point 	: doc['point'] + CHARGE_POINT[param.point]
						}
						res.end(JSON.stringify(result))
						db.close();
					});
				}
				else
				{
					result = {
						code 	: -3,
						message : "Invalidate Session"
					}
					res.end(JSON.stringify(result));
					db.close();
				}
			});
		});
	});
});

app.post('register', function(req, res)
{
	var param = req.body;

	var result = new Array();

	if(param.regId == "" || param.id == "" ||
		param.regId == null || param.id == null)
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
		db.createCollection('pushID', function(err, collection)
		{
			assert.equal(null, err);
			collection.findOne({regId : param.regId},function(err, doc)
			{
				assert.equal(null, err);
				if(doc == null)
				{
					collection.insert({regId : param.regId, id : param.id}, function(err, doc)
					{
						assert.equal(null, err);
						result = {
							code 	: 1,
							message : "Success register"
						}
						res.end(JSON.stringify(result));
						db.close();
					});
				}
				else
				{
					result = {
						code 	: -2,
						message : "Already register"
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