var http = require("http");
var url = require("url");
var fs = require('fs');
var multer  = require('multer')
var events = require('events');
var crypto = require('crypto');
var express = require('express');
var bodyParser = require('body-parser');
var gcm = require('node-gcm');

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
var PORT = 40002;
var COUPON_IMAGE_STORAGE = __dirname + '/couponImage/' 	// 쿠폰의 썸네일 이미지
var COUPON_FILE_STORAGE = __dirname + '/couponFile/'	// 쿠폰의 바코드 이미지, 파일, QR코드

var COUPON_TIME_PRICE = 100;
var COUPON_GPS_PRICE = 50;
var COUPON_DEFAULT_PRICE = 200;

app.get('/', function(req, res)
{
	res.end("Coupon Manage Start Port " + PORT);
});

//쿠폰 등록 위도, 경도
app.post('/couponRegisterGPS', function(req, res)
{
	var param 		= req.body;
	var reqFile 	= req.files['file'];
	var reqImage 	= req.files['image'];

	var result = new Array();

	if( reqImage == "" || reqImage == null )
	{
		result = {
			code 	: -4,
			message : "You have not append image"
		}
		res.end(JSON.stringify(result));
		return;
	}
	if(param.session == "" || param.type == "" || param.title == "" || param.desc == "" || param.lat == "" || param.lng == "" || param.tilt == "" || 
		param.session == null || param.type == null || param.title == null || param.desc == null || param.lat == null || param.lng == null || param.tilt == null )
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}
	if( param.tilt < 0 || param.tilt > 15)
	{
		result = {
			code 	: -5,
			message : "Out of range tilt"
		}
		res.end(JSON.stringify(result));
		return;
	}
	if(param.type != 'code' && param.type !='link' && param.type !='barcode' && param.type != 'qrcode' && param.type != 'file')
	{
		result = {
			code 	: -7,
			message : "Undefined type"
		}
		res.end(JSON.stringify(result));
		return;
	}

	if( param.type == 'link' || param.type == 'code' )
	{
		if( param.link == "" || param.link == null) 
		{
			result = {
				code 	: -2,
				message : "You have not written link or code"
			}
			res.end(JSON.stringify(result));
			return;
		}
		reqFile = new Array();
		reqFile.extension = null;
	}
	else
	{
		if( reqFile == "" || reqFile == null) 
		{
			result = {
				code 	: -3,
				message : "You have not append file"
			}
			res.end(JSON.stringify(result));
			return;
		}
		param.link = "";
	}

	db.open(function(err, db)
	{
		assert.equal(null, err);
		db.createCollection('userinfo', function(err, userCol)
		{
			assert.equal(null, err);
			userCol.findOne({session:param.session, api:'manage'},function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null) // session이 있으면
				{
					if(doc['point'] < COUPON_DEFAULT_PRICE )
					{
						result = {
							code 	: -8,
							message : "You have not point"
						}
						res.end(JSON.stringify(result));
						return;
					}
					db.createCollection('coupon', function(err, collection)
					{
						assert.equal(null, err);
						collection.find().toArray(function(err,docs)
						{
							var couponID;
							while(true)
							{
								var bFlag = false;
								couponID = randomValueHex(60);
								for( var v in docs )
								{
									if(docs['id'] == couponID)
									{
										bFlag = true;
										break;
									}
								}
								if(!bFlag)
									break;
							}

							fs.renameSync(reqImage.path, COUPON_IMAGE_STORAGE + couponID + '.' + reqImage.extension);
							if( param.type != 'link' && param.type != 'code' ) 
								fs.renameSync(reqFile.path, COUPON_FILE_STORAGE + couponID + '.' + reqFile.extension);

							//fs.unlinkSync(__dirname + '/' + reqImage.path);
							//fs.unlinkSync(__dirname + '/' + reqFile.path);

							collection.insert(
							{
								id 		: couponID,
								category: 'gps',
								type 	: param.type,
								title 	: param.title,
								desc	: param.desc,
								create 	: doc.id,
								link 	: param.link,
								lat 	: param.lat,
								lng		: param.lng,
								tilt	: param.tilt,
								imageEx	: reqImage.extension,
								fileEx	: reqFile.extension,
								active	: true
							},function(err,doc)
							{
								assert.equal(null, err);
								userCol.update({ session:param.session }, { $push: { coupon: couponID } }, function(err,doc)
								{
									assert.equal(null, err);
									result = {
										code 	: 1,
										message : "Success Coupon Register"
									}
									res.end(JSON.stringify(result));
									db.close();
								});
							}); // insert
						});
					});
				}
				else
				{
					result = {
						code 	: -6,
						message : "Invalidate Session"
					}
					res.end(JSON.stringify(result));
					db.close();
				}
			});
		});
	});
});

//쿠폰 시간 등록
app.post('/couponRegisterTime', function(req, res)
{
	var param 		= req.body;
	var reqFile 	= req.files['file'];
	var reqImage 	= req.files['image'];

	var result = new Array();

	if( reqImage == "" || reqImage == null )
	{
		result = {
			code 	: -4,
			message : "You have not append image"
		}
		res.end(JSON.stringify(result));
		return;
	}
	if(param.session == "" || param.type == "" || param.title == "" || param.desc == "" || param.beginT== "" || param.endT == "" || param.tilt == "" || 
		param.session == null || param.type == null || param.title == null || param.desc == null || param.beginT == null || param.endT == null || param.tilt == null )
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}
	if( param.tilt < 0 || param.tilt > 15)
	{
		result = {
			code 	: -5,
			message : "Out of range tilt"
		}
		res.end(JSON.stringify(result));
		return;
	}
	if(param.type != 'code' && param.type !='link' && param.type !='barcode' && param.type != 'qrcode' && param.type != 'file')
	{
		result = {
			code 	: -7,
			message : "Undefined type"
		}
		res.end(JSON.stringify(result));
		return;
	}

	if( param.type == 'link' || param.type == 'code' )
	{
		if( param.link == "" || param.link == null) 
		{
			result = {
				code 	: -2,
				message : "You have not written link or code"
			}
			res.end(JSON.stringify(result));
			return;
		}
		reqFile = new Array();
		reqFile.extension = null;
	}
	else
	{
		if( reqFile == "" || reqFile == null) 
		{
			result = {
				code 	: -3,
				message : "You have not append file"
			}
			res.end(JSON.stringify(result));
			return;
		}
		param.link = "";
	}

	db.open(function(err, db)
	{
		assert.equal(null, err);
		db.createCollection('userinfo', function(err, userCol)
		{
			assert.equal(null, err);
			userCol.findOne({session:param.session, api:'manage'},function(err, doc)
			{
				assert.equal(null, err);

				if(doc != null) // session이 있으면
				{
					if(doc['point'] < COUPON_DEFAULT_PRICE )
					{
						result = {
							code 	: -8,
							message : "You have not point"
						}
						res.end(JSON.stringify(result));
						return;
					}
					db.createCollection('coupon', function(err, collection)
					{
						assert.equal(null, err);
						collection.find().toArray(function(err,docs)
						{
							var couponID;
							while(true)
							{
								var bFlag = false;
								couponID = randomValueHex(60);
								for( var v in docs )
								{
									if(docs['id'] == couponID)
									{
										bFlag = true;
										break;
									}
								}
								if(!bFlag)
									break;
							}

							fs.renameSync(reqImage.path, COUPON_IMAGE_STORAGE + couponID + '.' + reqImage.extension);
							if( param.type != 'link' && param.type != 'code' ) 
								fs.renameSync(reqFile.path, COUPON_FILE_STORAGE + couponID + '.' + reqFile.extension);

							//fs.unlinkSync(__dirname + '/' + reqImage.path);
							//fs.unlinkSync(__dirname + '/' + reqFile.path);

							collection.insert(
							{
								id 		: couponID,
								category: 'time',
								type 	: param.type,
								title 	: param.title,
								desc	: param.desc,
								create 	: doc.id,
								link 	: param.link,
								beginT 	: param.beginT,
								endT	: param.endT,
								tilt	: param.tilt,
								imageEx	: reqImage.extension,
								fileEx	: reqFile.extension,
								active	: true
							},function(err,doc)
							{
								assert.equal(null, err);
								userCol.update({ session:param.session }, { $push: { coupon: couponID } }, function(err,doc)
								{
									assert.equal(null, err);
									result = {
										code 	: 1,
										message : "Success Coupon Register"
									}
									res.end(JSON.stringify(result));
									db.close();
								});
							}); // insert
						});
					});
				}
				else
				{
					result = {
						code 	: -6,
						message : "Invalidate Session"
					}
					res.end(JSON.stringify(result));
					db.close();
				}
			});
		});
	});
});

//쿠폰 삭제
app.post('/couponManageDelete', function(req, res)
{	
	var param = req.body;

	var result = new Array();

	if(param.id == "" || param.session == "" ||
		param.id == null || param.session == null)
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
			collection.findOne({session:param.session, api:'manage'},function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					for(var i = 0 ; i< doc['coupon'].length ; i++)
					{
						if(doc['coupon'][i] == param.id)
						{
							collection.update( {session:param.session,api:'manage'}, { $pull: { coupon: param.id } }, function(err, doc)
							{
								assert.equal(null, err);
								db.createCollection('coupon', function(err, couponCol)
								{
									assert.equal(null, err);
									couponCol.remove( {id:param.id}, function(err,doc)
									{
										assert.equal(null, err);
										result = {
											code 	: 1,
											message : "Success remove coupon"
										}
										res.end(JSON.stringify(result));
										db.close();
									});
								});
							});
							break;
						}
						else if( i + 1 == doc['coupon'].length )
						{
							result = {
								code 	: -3,
								message : "You have not this coupon"
							}
							res.end(JSON.stringify(result));
							db.close();
							return;
						}
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

//쿠폰 활성/비활성화
app.post('/couponActive', function(req, res)
{	
	var param = req.body;

	var result = new Array();

	if(param.session == "" || param.id == "" || param.active == "" ||
		param.session == null || param.id == null || param.active == null)
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
			collection.findOne({session:param.session, api:'manage'},function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					for(var i = 0 ; i< doc['coupon'].length ; i++)
					{
						if(doc['coupon'][i] == param.id)
						{
							db.createCollection('coupon', function(err, couponCol)
							{
								assert.equal(null, err);
								var bool;
								if(param.active == 1) bool = true;
								else bool = false;
								couponCol.update( {id:param.id},{$set:{active:bool}}, function(err,doc)
								{
									assert.equal(null, err);
									result = {
										code 	: 1,
										message : "Success change active coupon"
									}
									res.end(JSON.stringify(result));
									db.close();
								});
							});
							break;
						}
						else if( i + 1 == doc['coupon'].length )
						{
							result = {
								code 	: -3,
								message : "You have not this coupon"
							}
							res.end(JSON.stringify(result));
							db.close();
							return;
						}
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

//쿠폰 이미지 수정
app.post('/couponImageModify', function(req, res)
{	
	var param = req.body;
	var reqImage = req.files['image'];

	var result = new Array();
});

//쿠폰 파일 수정
app.post('/couponFileModify', function(req, res)
{	
	var param = req.body;
	var reqFile = req.files['file'];

	var result = new Array();
	
});

//세션으로 쿠폰 정보들 받아오기
app.get('/couponGet', function(req, res)
{
	var parsedUrl = url.parse(req.url, true);
	var queryAsObject = parsedUrl.query;
	var jsonData = JSON.parse(JSON.stringify(queryAsObject));

	var result = new Array();

	if(jsonData.session == "" || jsonData.session == null)
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}

	// 관리자 info에서 관리자 계쩡 안에있는 Coupon list를 반환한다.

	db.open(function(err, db)
	{
		assert.equal(null, err);
		db.createCollection('userinfo', function(err, collection)
		{
			assert.equal(null, err);
			collection.findOne({session:jsonData.session}, function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					db.createCollection('coupon', function(err, couponCol)
					{
						assert.equal(null, err);
						couponCol.find({ id: { $in : doc['coupon'] }} ).toArray(function(err,docs)
						{
							assert.equal(null, err);
							result = {
								code 	: 1,
								message : "Success return coupon",
								coupon 	: docs
							}
							res.end(JSON.stringify(result));
							db.close();
						});
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
					return;
				}
			});
		});
	});
});


// 사용자 쿠폰 리스트에서 쿠폰 추가
app.post('/couponAdd', function(req, res)
{	
	var param = req.body;

	var result = new Array();

	if(param.session == "" || param.id == "" || 
		param.session == null || param.id == null)
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
		db.createCollection('coupon', function(err, collection)
		{
			assert.equal(null, err);
			collection.findOne({id:param.id}, function(err, coupon)
			{
				assert.equal(null, err);
				if(coupon != null)
				{
					if(coupon['active'] == true)
					{
						db.createCollection('userinfo', function(err, userCol)
						{
							assert.equal(null, err);
							userCol.findOne({session:param.session}, function(err, user)
							{
								assert.equal(null, err);
								if(user != null)
								{
									for(var i = 0 ; i < user['coupon'].length ; i++)
									{
										if(user['coupon'][i] == param.id)
										{
											result = {
												code 	: -7,
												message : "You already have this coupon"
											}
											res.end(JSON.stringify(result));
											db.close();
											return;
										}
									}
									userCol.findOne({id:coupon.create, api:'manage'},function(err, doc)
									{
										assert.equal(null, err);
										if(doc != null)
										{
											if(doc['point'] >= COUPON_DEFAULT_PRICE)
											{
												var price = COUPON_GPS_PRICE;
												if(coupon['category'] == 'time') price = COUPON_TIME_PRICE;
												userCol.update(doc,{$inc : {point:-price}}, function(err, temp)
												{
													assert.equal(null, err);
													userCol.update({ session:param.session }, { $push: { coupon: param.id } }, function(err, temp)
													{
														assert.equal(null, err);
														result = {
															code 	: 1,
															message : "Success add coupon"
														}
														res.end(JSON.stringify(result));

														if(doc['point'] - price < COUPON_DEFAULT_PRICE)
														{
															collection.update({ create : doc['id'] }, { $set : { active : false } }, { multi : true }, function(err, temp)
															{
																assert.equal(null, err);
																PushManage("관리자 경고", "잔여 포인트가 " + COUPON_DEFAULT_PRICE + "포인트 미만이므로 모든 쿠폰이 비활성화 됩니다.", function(err, result)
																{
																	assert.equal(null, err);
																	db.close();
																});
															});
														}
														else
															db.close();
													});
												});
											}
											else
											{
												result = {
													code 	: -6,
													message : "Coupon creator not enough point"
												}
												res.end(JSON.stringify(result));
												db.close();
											}
										}
										else
										{
											result = {
												code 	: -5,
												message : "Coupon creator has been disappeared"
											}
											res.end(JSON.stringify(result));
											db.close();
										}
									});
								}
								else
								{
									result = {
										code 	: -4,
										message : "Invalidate session"
									}
									res.end(JSON.stringify(result));
									db.close();
								}
							});
						});
					}
					else
					{
						result = {
							code 	: -3,
							message : "Coupon is deactivated"
						}
						res.end(JSON.stringify(result));
						db.close();
					}
				}
				else
				{
					result = {
						code 	: -2,
						message : "Undefined coupon ID"
					}
					res.end(JSON.stringify(result));
					db.close();
				}
			});
		});
	});
});

// 사용자 쿠폰 리스트에서 쿠폰 삭제
app.post('/couponDelete', function(req, res)
{	
	var param = req.body;

	var result = new Array();

	if(param.session == "" || param.id == "" || 
		param.session == null || param.id == null)
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
			collection.findOne({session:param.session, api:{$in: ['basic','facebook']} },function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{
					for(var i = 0 ; i< doc['coupon'].length ; i++)
					{
						if(doc['coupon'][i] == param.id)
						{
							collection.update( {session:param.session,api:{$in: ['basic','facebook']} }, { $pull: { coupon: param.id } }, function(err, doc)
							{
								assert.equal(null, err);
								result = {
									code 	: 1,
									message : "Succes coupon delete"
								}
								res.end(JSON.stringify(result));
								db.close();
								return;
							});
							break;
						}
						else if( i + 1 == doc['coupon'].length )
						{
							result = {
								code 	: -3,
								message : "You have not this coupon"
							}
							res.end(JSON.stringify(result));
							db.close();
							return;
						}
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
					return;
				}
			});
		});
	});
});

/*
//쿠폰 아이디로 정보 받아오기
app.get('/couponGetInfo', function(req, res)
{
    var parsedUrl = url.parse(req.url, true);
    var queryAsObject = parsedUrl.query;
    var jsonData = JSON.parse(JSON.stringify(queryAsObject));

	var result = new Array();

});*/

//쿠폰 아이디로 이미지 받아오기
app.get('/couponGetImage', function(req, res)
{
	var parsedUrl = url.parse(req.url, true);
	var queryAsObject = parsedUrl.query;
	var jsonData = JSON.parse(JSON.stringify(queryAsObject));

	var result = new Array();

	if(jsonData.id == "" || jsonData.id == null)
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}
	fs.exists(COUPON_IMAGE_STORAGE + jsonData.id, function(exists)
	{
		if (exists)
			res.sendFile(COUPON_IMAGE_STORAGE + jsonData.id);
		else
		{
			result = {
				code 	: -2,
				message : "Image not exists"
			}
			res.end(JSON.stringify(result));
		}
	});
});

//쿠폰 아이디로 파일 받아오기
app.get('/couponGetFile', function(req, res)
{
	var parsedUrl = url.parse(req.url, true);
	var queryAsObject = parsedUrl.query;
	var jsonData = JSON.parse(JSON.stringify(queryAsObject));

	var result = new Array();

	if(jsonData.id == "" || jsonData.session == "" ||
		jsonData.id == null || jsonData.session == null)
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
			collection.findOne({session:jsonData.session}, function(err, doc)
			{
				assert.equal(null, err);
				console.log("session : " + jsonData.session)
				if(doc != null)
				{
					for(var i = 0 ; i< doc['coupon'].length ; i++)
					{
						var _id = jsonData.id.replace(/\.[^/.]+$/, "")
						if(_id == doc['coupon'][i])
						{
							fs.exists(COUPON_FILE_STORAGE + jsonData.id, function(exists)
							{
								if (exists)
								{
									res.sendFile(COUPON_FILE_STORAGE + jsonData.id);
									db.close();
								}
								else
								{
									result = {
										code 	: -3,
										message : "File not exists or Coupon type is link or code"
									}
									res.end(JSON.stringify(result));
									db.close();
								}
							});
							break;
						}
						else if( i - 1 == doc['coupon'].length )
						{
							result = {
								code 	: -4,
								message : "You have not this coupon"
							}
							res.end(JSON.stringify(result));
							db.close();
							return;
						}
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
					return;
				}
			});
		});
	});
});

function PushManage ( title, desc, callback )
{
	var registrationIds = [];

	var message = new gcm.Message();

	var sender = new gcm.Sender('sender-key');

	db.open(function(err, db)
	{
		db.createCollection('pushID', function(err,collection)
		{
			assert.equal(null, err);
			collection.find().toArray(function(err, docs)
			{
				assert.equal(null, err);
				for(var v in docs)
				{
					registrationIds.push(v.regId);
				}

				message.addData('title', title);
				message.addData('desc', desc);
				message.collapseKey = 'tiltcode';
				message.delayWhileIdle = true;
				message.timeToLive = 3;

				if(registrationIds.length < 1)
					callback(null, null);
				else
				{
					sender.send(message, registrationIds, 4, function (err, result)
					{
						callback(err, result);
					});
				}
			});
		});
	});
}
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