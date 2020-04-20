Global MapMesh
Global MapBody
Global MapWater
Global PackLoaded
Global MapPack
Global AlphaEnt,Water

Global menumesh
Function LoadMenuBackuground()
	
	menumesh=LoadDeferredMesh(MapsPatch$+"menu\map.3DS",1)
	LoadAudioSystem(MapsPatch$+"menu\Sounds.ini")
	LoadLightPatch(NoEditorLights,"menu\")
	UpdateDeferredShadows()
	
End Function



Function UNLoadMenuBackuground()
	xFreeEntity menumesh
	DeleteDeferredRecivers()	
	DeleteMapSounds()
	FreeDeferredLights()
End Function


Function LoadMapDP(DirPack$,PakName$)
	
	StatusBarPercent=0
	Status_bar(5)
	
	PackOrDir=xFileSize(DirPack$+PakName$)
	Status_bar(5)
	PackLoaded=0
	DebugLog PackOrDir
	If PackOrDir<>0
		PackLoaded=1
		MapPack = xMountPackFile (DirPack$+PakName$,DirPack$,"")
	EndIf 
	Status_bar(5)
	
	MapMeshDir$=MapsPatch$+DirPack$+"Map.3ds"
	MapBodyDir$=MapsPatch$+DirPack$+"Body.3ds"
	
	
	
	LightsMeshDir$  =MapsPatch$+DirPack$+"Lights.ini"
	DynamicMeshDir$ =MapsPatch$+DirPack$+"dynamic.3DS"
	ParticleMeshDir$=MapsPatch$+DirPack$+"Particle.ini"
	TriggersMeshDir$=MapsPatch$+DirPack$+"Triggers.ini"
	LightINIPatch$	=MapsPatch$+DirPack$+"save.ini"
	
	
	
	Status_bar(5)
	
	
	
	If xFileSize(MapMeshDir$)>0
		
		
			MapMesh = LoadDeferredMesh(MapMeshDir$,1)
			
		
		
		
		xEntityPickMode MapMesh ,2
		
		Status_bar(30)
		DebugLog "MapLoaded " +MapMesh
		
		
		;If xFileSize(MapBodyDir$)>0
		;	MapBodyMesh  		= xLoadMesh (MapBodyDir$)
		;Else	
		MapBodyMesh  		= xLoadMesh (MapMeshDir$)
		;EndIf 	
		
		If MapBodyMesh<>0
			MapBody					=BodyCreateMesh%(MapBodyMesh,0)
			Local MaterialStatic%=pxCreateMaterial()
			pxMaterialSetStFriction MaterialStatic,.2
			pxMaterialSetDyFriction MaterialStatic,.2
			pxMaterialSetRestitution MaterialStatic,0
			pxMaterialSetToBody MapBody,MaterialStatic
			xFreeEntity               MapBodyMesh
			DebugLog "MapBodyCreated"
		EndIf 
		Status_bar(30)
		
		
		pxBodySetFlagRayCast (MapBody,1)
		
	EndIf 
	
	
	LoadLightPatch(NoEditorLights,DirPack$)
	LoadPXMap(DirPack$)
	
	
	DynamicMesh=xLoadMeshWithChild(DynamicMeshDir$)
		If DynamicMesh<>0
			
			Local i, child 
			For i=0 To xCountChildren(DynamicMesh)  
				child=xGetChild(DynamicMesh,i)  
				If child<>0 
					
					If Instr(Lower(xEntityName(child)),"doorup")>0 Then  
						CreateDoor(child)
					Else 
						body=CreateMeshAsPhysXObj(child,10,Lower(xEntityName(child)))
						xEntityPickMode GetMeshFromBody(Body),2
						pxBodySetSleepWakeUp(body, 10000)
						
						
						If Instr(Lower(xEntityName(child)),"lig")>0 Then  
							lightg=CreateDeferredLight(2,GetMeshFromBody(Body))
							DeferredLightColor(lightg,120, 190,120)
							DeferredLightRange(lightg,80)	
						EndIf 
						
						
						
						If Instr(Lower(xEntityName(child)),"dm")>0 Then  
							pxBodySetFrozen(body,1)
							DebugLog "static"
						EndIf 
						
						If Instr(Lower(xEntityName(child)),"rot")>0 Then  
							SetObjKinematic(body,child,1,1)
							DebugLog "rot"
						EndIf 
						
						TempLit=GetLightInRad(child)
						If TempLit<>0
							DebugLog "I got light in radius"
							;xEntityParent TempLit,GetMeshFromBody(Body)
							
							;SrtLightParrent(GetLightFromEnt(TempLit),GetMeshFromBody(Body))
						EndIf 
						
						
						If Instr(Lower(xEntityName(child)),"lift")>0 Then  
							xEntityPickMode GetMeshFromBody(Body),0
							
							lightg=CreateDeferredLight(1,GetMeshFromBody(Body))
							DeferredLightColor(lightg,255, 219,122)
							DeferredLightRange(lightg,80)	
							DeferredLightShadows(lightg,1)
							DebugLog "NILIFT"+TempLit+"   "+GetLightFromEnt(TempLit)
							
							SetObjKinematic(body,child,2,10)
							lift=1
							DebugLog "lift"
						EndIf 
						
						If lift=0
						pxBodySetPosition body ,xEntityX(child,1),xEntityY(child,1),xEntityZ(child,1)
						pxBodySetRotation (body, xEntityPitch (child,1), xEntityYaw (child,1), xEntityRoll (child,1))
					EndIf 
					
					
					
						
						
						If Instr(Lower(xEntityName(child)),"doorho")>0 Then  
							j= pxJointCreateHinge(0,body, xEntityX(child,1),xEntityY(child,1),xEntityZ(child,1),0, 5, 0)
							pxJointHingeSetLimit(j, -90,90)
						EndIf 
						
						If Instr(Lower(xEntityName(child)),"joi")>0 Then  
							;j= pxJointCreateHinge(0,body, xEntityX(child,1),xEntityY(child,1),xEntityZ(child,1),0, 5, 0)
						;	j=pxJointCreateDistance(0,body, xEntityX(child,1),xEntityY(child,1),xEntityZ(child,1),0, 5, 0)
							
							;j = pxJointCreateDistance(0,body, xEntityX(child,1),xEntityY(child,1),xEntityZ(child,1),0, 2, 0)
							;pxJointDistanceSetPoint(j, 0.5, 1)
							
							;pxJointDistanceSetPoint(j, 0, 00.1)
							
							joint1=pxJointCreateSpherical(0,body,xEntityX(child,1),xEntityY(child,1),xEntityZ(child,1),  0, 1,0)
							
							
							;pxJointHingeSetLimit(j, -90,90)
						EndIf 
						
						
						
						
						
						
							
					EndIf  
				EndIf 
			Next 
			
			
			
			xFreeEntity DynamicMesh
		Else 
		EndIf 
		
		If xFileSize(MapsPatch$+DirPack$+"water.fbx")>0
			Water=xLoadMesh(MapsPatch$+DirPack$+"water.fbx")
			If Water<>0
				AddAlphaEntity(Water,1,1,1,0)
				xSetEffectTechnique Water, "Water"
				tTextureNormalW% = xLoadTexture(TexturesPatch$+"Water_NormalW.png")
				tTextureNormalN% = xLoadTexture(TexturesPatch$+"Water_NormalN.png")
				xSetEffectTexture Water,	"tNormalW", tTextureNormalW
				xSetEffectTexture Water,	"tNormalN", tTextureNormalN
				
				xSetEffectTexture Water,	"envTexture", xLoadTexture("Deferred/textures/1ac1d923.dds",1 + 128)
				
				Foam = xLoadTexture(TexturesPatch$+"Foam.png")
				xSetEffectTexture Water,"FoamTexture", Foam
			EndIf 
			
			
		EndIf
		
		
		If xFileSize(MapsPatch$+DirPack$+"AlphaEnt.3ds")>0
			AlphaEnt=xLoadMesh(MapsPatch$+DirPack$+"AlphaEnt.3ds")
			If AlphaEnt<>0
				xEntityFX AlphaEnt,16
				AddAlphaEntity(AlphaEnt,0.5,0.1,0,4)
			EndIf 
		EndIf
		
		
		If xFileSize(MapsPatch$+DirPack$+"Sounds.ini")
			LoadAudioSystem(MapsPatch$+DirPack$+"Sounds.ini")
		EndIf 
		
		
		
		ParticleMeshMap= xLoadAnimMesh(MapsPatch$+DirPack$+"ParticleMesh.3ds")
		If ParticleMeshMap<>0
			GetParticles(ParticleMeshMap) 
			xFreeEntity ParticleMeshMap
		EndIf 
		
		
		LightShafts= xLoadAnimMesh(MapsPatch$+DirPack$+"shafts.fbx")
		If LightShafts<>0
			For lsc=0 To xCountChildren(LightShafts)  
				ShaftEntity=xGetChild(LightShafts,lsc)  
				If ShaftEntity<>0  
					
					AddScatteringMesh(ShaftEntity,2,64)
					AddAlphaEntity(ShaftEntity,1,10,1,1)
		;	AddScatteringMesh(ShaftEntity,1.1,128)
			;AddScatteringMesh(ShaftEntity,0.9,32);
				EndIf  
			Next  
			AddAlphaEntity(LightShafts,10000)
		EndIf 
		Status_bar(10)
		
		Status_bar(10)
		
		LoadRefMap(DirPack$)
		
		
		;Chapter_1_Scripts(MapsPatch$+DirPack$)
		xUnmountPackFile MapPack
		
	
End Function 

Function GetParticles(ent)  
    Local i, child 
    For i=0 To xCountChildren(ent)  -1
        child=xGetChild(ent,i)  
        If child<>0  
			NE=CreateEmitter(0,0,0,1) 
			xRotateEntity(NE, xEntityRoll(child,1),  xEntityPitch(child,1), xEntityYaw (child,1),1)
			xPositionEntity(NE,xEntityX(child,1),xEntityY(child,1),xEntityZ(child,1))
		EndIf 
Next  
End Function 


Type TDoor
	Field Mesh
	Field Body
	Field DPivot
	Field Frame#
	Field x
	Field y
	Field z
	
	Field lastS	Field Sound
End Type

Function CreateDoor(DoorMesh)
	;============================================
	If DoorMesh<>0
	dr.TDoor=New TDoor
	dr\Mesh=xCopyEntity(DoorMesh)
	dr\body=BodyCreateHull(DoorMesh,100) 
	SetDeferredMesh(dr\Mesh,1)
	dr\DPivot=xCreatePivot()
	pxKinematicSet(dr\body)
	dr\x=xEntityX(dr\Mesh)
	dr\y=xEntityY(dr\Mesh)
	dr\z=xEntityZ(dr\Mesh)
	
	dr\Sound=xLoad3DSound(SoundPatch$+"door_closed.mp3")
	
	pxBodySetPosition dr\body ,xEntityX(dr\Mesh),xEntityY(dr\Mesh),xEntityZ(dr\Mesh)
	pxBodySetRotation (dr\body, xEntityPitch (dr\mesh,1), xEntityYaw (dr\mesh,1), xEntityRoll (dr\mesh,1))
	
	xPositionEntity dr\DPivot,xEntityX(dr\Mesh,1),xEntityY(dr\Mesh,1),xEntityZ(dr\Mesh,1),1
	pxKinematicSetPosition(dr\body,xEntityX(dr\Mesh),xEntityY(dr\Mesh),xEntityZ(dr\Mesh))
EndIf 
End Function

Function UpdateDoor(Entity)
	For dr.TDoor=Each TDoor
		
		
		sound=0
		
		If  xEntityDistance(Entity,dr\DPivot)<30 
			dr\Frame=dr\Frame+0.5
			
			If dr\Frame>35 Then
				dr\Frame=35
			EndIf 
			
			dr\lastS=1
			
		Else
			dr\Frame=dr\Frame-0.5
			
			If dr\Frame<0 Then 
				dr\Frame=0  
				
				If dr\lastS=1
					dr\lastS=0
					sound=1
				EndIf 
				
			EndIf 
			
			
			
		EndIf
		
		
		
		
		If sound=1 
			xEmitSound dr\Sound,dr\Mesh
		EndIf 
		xPositionEntity dr\Mesh,dr\x,dr\y+dr\Frame,dr\z
		pxKinematicSetPosition(dr\body,xEntityX(dr\Mesh),xEntityY(dr\Mesh),xEntityZ(dr\Mesh))	
		
		xBodySetEntity(dr\Mesh,dr\body)
	Next
End Function

Function FreeMap()
	xFreeEntity MapMesh
	pxDeleteBody MapBody
	
	
	
	If Water<>0
		;DeleteAlphaEntity(Water)
	;	xFreeEntity Water
	EndIf 
	
End Function 
;~IDEal Editor Parameters:
;~C#Blitz3D
;~IDEal Editor Parameters:
;~C#Blitz3D