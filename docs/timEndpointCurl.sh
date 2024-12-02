curl -X POST -H "Content-Type: application/json" -H "Cache-Control: no-cache" -H "Postman-Token: 4c014aac-3597-a40d-e992-271d377d6d4c" -d '{
	"timContent": {
		"msgcnt": "1",
		"timestamp": "op",
		"packetID": "op",
		"urlB": "op",
		"travelerDataFrame": [{
			"header": {
				"sspindex": "1",
				"travelerInfoType": "1",
				"msgId": {
					"FurtherInfoID": "1",
					"RoadSignID": {
						"position3D": {
							"latitude": "1",
							"longitude": "1",
							"elevation": "1",
							"regional": "op"
						},
						"HeadingSlice": "1010011010010100",
						"MUTCDCode": "op",
						"MsgCRC": "op"
					}
				},
				"DYear": "op",
				"MinuteOfTheYear": "0",
				"MinutesDuration": "0",
				"SignPriority": "0"
			},
			"region": {
				"sspindex": "0",
				"GeographicalPath": [{
					"name": "op",
					"id": "op",
					"anchor": "op",
					"laneWidth": "op",
					"diretionality": "op",
					"closedPath": "op",
					"direction": "op",
					"description": [{
						"path": [{
							"scale": "op",
							"offset": [{
								"xy": [{
									"nodes": [{
										"delta": [{
											"node-LL1": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL2": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL3": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL4": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL5": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL6": [{
												"lon": "20",
												"lat": "21"
											}]
										}],
										"attributes": "op"
									}],
									"computed": [{
										"referenceLaneID": "1",
										"offsetXaxis": [{
											"small": "1",
											"large": "1"
										}],
										"offsetYaxis": [{
											"small": "1",
											"large": "1"
										}]
									}]
								}],
								"ll": [{
									"nodes": [{
										"delta": [{
											"node-LL1": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL2": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL3": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL4": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL5": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL6": [{
												"lon": "20",
												"lat": "21"
											}]
										}],
										"attributes": "op"
									}]
								}]
							}]
						}],
						"geometry": [{
							"direction": "1001101100100100",
							"circle": [{
								"center": [{
									"latitude": "1",
									"longitude": "1",
									"elevation": "1",
									"regional": "op"
								}],
								"radius": "3",
								"units": "4"
							}]
						}],
						"oldRegion": [{
							"direction": "1001101100100100",
							"area": [{
								"shapePointSet": [{
									"nodes": [{
										"delta": [{
											"node-LL1": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL2": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL3": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL4": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL5": [{
												"lon": "20",
												"lat": "21"
											}],
											"node-LL6": [{
												"lon": "20",
												"lat": "21"
											}]
										}],
										"attributes": "op"
									}],
									"computed": [{
										"referenceLaneID": "1",
										"offsetXaxis": [{
											"small": "1",
											"large": "1"
										}],
										"offsetYaxis": [{
											"small": "1",
											"large": "1"
										}]
									}]
								}],
								"circle": [{
									"center": [{
										"latitude": "1",
										"longitude": "1",
										"elevation": "1",
										"regional": "op"
									}],
									"radius": "3",
									"units": "4"
								}],
								"regionPointSet": [{
									"nodeList": [{
										"xOffset": "1",
										"yOffset": "1"
									}]
								}]
							}]
						}]
					}]
				}]
			},
			"content": {
				"doNotUse3": 0,
				"doNotUse4": 0,
				"contentType": {
					"advisory": [{
						"ITISCodes": "268",
						"ITIStext": "Speed Limit"
					} ],
					"workZone": [],
					"genericSign": [],
					"speedLimit": [],
					"exitService": []
				},
				"URL-Short": "op",
				"regional": "op"
			}
		}],
		"regional": "op"
	},
	"RSUs": [{
		"target": "127.0.0.1",
		"username": "v3user",
		"password": "password",
		"retries": "1",
		"timeout": "2000"
	}], 
	"snmp": {
		"rsuid": "8300",
		"msgid": "31",
		"mode": "1",
		"channel": "178",
		"interval": "1",
		"deliverystart": "010114111530",
		"deliverystop": "010114130000",
		"enable": "1",
		"status": "4"
	}
}' "http://localhost:8080/tim"
