Global hands
Global  player_body,Player_model,Player_pxBody
Global PlayerSteps =8,camp,bobpower =1
Global mouseyspd#,mousexspd#
Global zoomstate#,zoom#

Global light,ray 
Global Camera_AngleRange#=70
Global Width2=w/2		
Global Height2=h/2
Global FL_Pitch#,FL_Yaw#,FL_Roll#,FL_XSpeed#,FL_YSpeed#,FL_ZSpeed#
Global V#

Global stamina#=100
Global Player_legs_trig,Player_legs,Player_head,Player_head_trig,Player_head_trig2
Global PlayerInertiaX#,PlayerInertiaY#


Global BAGROT
Global mxs#,mys#
Global camxa#,camya#
Global NoClipSpeed=10

Global a1#,Player_pxBodyD
Global Player_forceWalkF#=110
Global Player_forceWalkS#=110
Global Player_forceWalkB#=100
Global Player_forceJump#=380
Global Player_is_walk=False
Global Player_is_jump=False
Global Player_is_duck=False
Global Player_pxBodyMat
Global Player_on_floor=False
Global Player_type=False
Global Player_size#=2
Global Player_health=100
Global Player_stamina#=100
Global FlashLightEnable=0
Global FlashLight
Global ForHandBody,ShowHands,HandspointerAlpha#

Function freeplayer()
	xFreeEntity Player_model
	pxDeleteBody Player_pxBody
	;pxDeleteBody Player_head
	pxDeleteBody Player_legs_trig
End Function

Function createplayer()
	Player_model=xLoadMesh(PlayerModelPatch$+"Player.3DS")  
	xEntityAlpha Player_model,0.2
	xHideEntity Player_model
	
	Player_legs_trig = pxTriggerCreateSphere(5)
	pxBodySetFlagRayCast (Player_legs_trig,0)
	
	Player_pxBody=BodyCreatehull%(Player_model,12);pxBodyCreateCapsule(14,10,8)
	pxBodySetFrozenRot(Player_pxBody,1)	
	pxBodySetFlagRayCast (Player_pxBody,0)
	pxBodySetFlagTriggertable(Player_pxBody, 0)
	pxBodySetFlagGravity(Player_pxBody, 0.6)
	
	
	pxBodySetCollisionGroup(Player_pxBody, 2)
	Player_pxBodyMat=pxCreateMaterial()								; ?????????? ?????? ??? ?????????? ???????
	pxMaterialSetDyFriction(Player_pxBodyMat,0.0)					; ???????????? ??????
	pxMaterialSetRestitution(Player_pxBodyMat,0.0)				; ?????????
	pxMaterialSetFrictionCombineMode(Player_pxBodyMat,1)		; ??????????????? ??????
	pxMaterialSetToBody(Player_pxBody,Player_pxBodyMat)
	pxBodySetPosition Player_pxBody,0,20,0
	
	
	player_body=Player_pxBody
	xCameraZoom camera ,1.7
	Flash_Pivot=xCreatePivot(camera)
	
	FlashColor=200
	FlashLight=CreateDeferredLight(2,Flash_Pivot)
	DeferredLightColor(FlashLight,FlashColor, FlashColor, FlashColor)
	DeferredLightRange(FlashLight,190)
	DeferredLightShadows(FlashLight,1,0)
	DeferredLightConeAngles(FlashLight,0.95,400)

	
	xMoveEntity Flash_Pivot,2,-3,0
	
	
	;DeferredLightState(FlashLight,0)
	FlashLightEnable=0
End Function


Function BodyControl()
	
	;campic=xCameraPick(camera,xMouseX(),xMouseY())
	pxRaySetPosition(ray, xEntityX(camera,1), xEntityY(camera,1),xEntityZ(camera,1))
	xTFormVector 0,0,1,camera, 0
	DirX# = xTFormedX()
	DirY# = xTFormedY()
	DirZ# = xTFormedZ()
	pxRaySetDir(ray,DirX,DirY,DirZ)
	raydist=pxRayGetDistance(ray,1)
	PickedBody = pxRayGetBody(ray,1)
	ShowHands=0
	
	If raydist<35  And raydist>15 
		If PickedBody<>0 And ForHandBody=0
			ShowHands=1
		EndIf 
	EndIf 
	
	
	
	XkHt33=xkht18
	
	
	If XkHt33 And ForHandBody=0
		DebugLog "take "+ForHandBody+"   "
		pxRaySetPosition(ray, xEntityX(camera,1), xEntityY(camera,1),xEntityZ(camera,1))
		xTFormVector 0,0,1,camera, 0
		DirX# = xTFormedX()
		DirY# = xTFormedY()
		DirZ# = xTFormedZ()
		pxRaySetDir(ray,DirX,DirY,DirZ)
		raydist=pxRayGetDistance(ray,1)
		ForHandBody = pxRayGetBody(ray,1)
		
		xPositionEntity BodyPos,pxRayGetPickX(ray,0),pxRayGetPickY(ray,0),pxRayGetPickZ(ray,0),1
		
		DebugLog raydist
		If raydist>50 Or raydist<15
			
			ForHandBody=0
			
		Else 
			HideWeapon()
		EndIf 
		
		
		XkHt33=0
	EndIf 
	
	If  ForHandBody<>0
		If XkHt33<>0
		;	pxBodySetLinearDamping(ForHandBody,0)
			;pxBodySetAngularSpeed(ForHandBody ,0,0,0)
			;;pxBodySetLocalLinearSpeed(ForHandBody ,0,0,0)
			;ForHandBody=0
		End If
	End If
	
	If  ForHandBody<>0
		If XkHt33<>0
			pxBodySetLinearDamping(ForHandBody,0)
			pxBodySetAngularSpeed(ForHandBody ,0,0,0)
			pxBodySetLocalLinearSpeed(ForHandBody ,0,0,0)
			ForHandBody=0
		End If
	End If
	
	If ForHandBody<>0 And  msh1=1
		xTFormVector 0,0,1,camera, 0
		DirX# = xTFormedX()
		DirY# = xTFormedY()
		DirZ# = xTFormedZ()
		pxBodySetLinearDamping(ForHandBody,0)
		pxBodySetAngularSpeed(ForHandBody ,0,0,0)
		pxBodySetLocalLinearSpeed(ForHandBody ,0,0,0)
		multipler=250
		pxBodyAddForceAtPos ForHandBody, DirX*pxBodyGetMass(ForHandBody)*multipler,DirY*pxBodyGetMass(ForHandBody)*multipler,DirZ*pxBodyGetMass(ForHandBody)*multipler, 0, 0,0,1		
		ForHandBody=0
	EndIf 
	If ForHandBody<>0 Then
		;pxBodyMoveToPoint(ForHandBody,xEntityX(BodyPos,1),xEntityY(BodyPos,1),xEntityZ(BodyPos,1), 2,  5,5,5  ,0.8,8)
		pxBodyMoveToPoint(ForHandBody,xEntityX(BodyPos,1),xEntityY(BodyPos,1),xEntityZ(BodyPos,1), 2,  2,2,2  ,0.8,32)
		pxBodySetLinearDamping(ForHandBody,10)
	End If
End Function 


Function UpdateFlyCam(pos=1)
	
	
	mousespeed#       = 0.05 
	camerasmoothness# = 4.5
	If pos=1
	If xKeyDown(KEY_W) Then xMoveEntity camera,  0,  0,  NoClipSpeed go=1
	If xKeyDown(KEY_S) Then xMoveEntity camera,  0,  0, -NoClipSpeed go=1
	If xKeyDown(KEY_A) Then xMoveEntity camera, -NoClipSpeed,  0,  0 go=1
	If xKeyDown(KEY_D) Then xMoveEntity camera,  NoClipSpeed  ,0,  0 go=1
EndIf 
	
mxs#= CurveValue(mouseSpeedX * mousespeed, mxs, camerasmoothness)
	mys# = CurveValue(mouseSpeedY * mousespeed, mys, camerasmoothness)
	
	camxa# = camxa - mxs Mod 360
	camya# = camya + mys
	If camya < -89 Then camya = -89
	If camya >  89 Then camya =  89
	xMoveMouse xGraphicsWidth() / 2, xGraphicsHeight() / 2
	xRotateEntity camera, camya, camxa, 0.0
	If pos=1
	pxBodySetPosition Player_pxBody,xEntityX(camera),xEntityY(camera),xEntityZ(camera)
	pxBodySetLocalLinearSpeed(Player_pxBody,0,0,0)
EndIf
End Function


Function StopPlayer()
	pxBodySetLocalLinearSpeed(Player_pxBody,0,0,0)
End Function


Function updateplayer(updatecam)
	
	If Player_health>100
		Player_health=100
	EndIf 
	
	If Player_stamina#>200
		Player_stamina#=200
	EndIf 
	
	If Player_stamina#<0
		Player_stamina#=0
	EndIf 
	
	
	pxKinematicSetPosition (Player_legs_trig,xEntityX(Player_model,1),xEntityY(Player_model,1)-15,xEntityZ(Player_model,1))
	
	
	If xkht42  And Player_is_walk=True  And Player_on_floor=True
		Player_stamina#=Player_stamina#-0.5
		If Player_stamina#>1
			Player_forceWalkF#=380
			Player_forceWalkS#=380
			Player_forceWalkB#=300
			bobpower =2
			camp=8
			zoomstate#=-0.5
		Else 
			zoomstate#=0
			bobpower =1
			camp=5
			Player_forceWalkF#=210
			Player_forceWalkS#=210
			Player_forceWalkB#=200
			Player_stamina#=Player_stamina#+0.1
		EndIf 
	Else
		zoomstate#=0
		bobpower =1
		camp=5
		Player_forceWalkF#=210
		Player_forceWalkS#=210
		Player_forceWalkB#=200
		Player_stamina#=Player_stamina#+0.4
	EndIf	
	
	If stamina#<0 stamina#=0
		If zoom#>zoomstate#  
			zoom#=zoom#-0.05
		EndIf 
		If zoom#<zoomstate# 
			zoom#=zoom#+0.05
		EndIf
		xCameraZoom camera,1+ zoom#*0.3
		x#=pxBodyGetPositionX#(Player_pxBody)
		y#=pxBodyGetPositionY#(Player_pxBody)
		z#=pxBodyGetPositionZ#(Player_pxBody)
		roty#=pxBodyGetRotationYaw(Player_pxBody)
		forcex#=pxBodyGetLocalLinearSpeedX#(Player_pxBody)
		forcey#=pxBodyGetLocalLinearSpeedY#(Player_pxBody)
		forcez#=pxBodyGetLocalLinearSpeedZ#(Player_pxBody)
		
		If y#<-2200
			pxBodySetPosition Player_pxBody,0,100,0
		EndIf 
		
	;pxKinematicSetPosition (Player_head_trig,x#,y#=-2,z#)
	;pxKinematicSetPosition (Player_legs_trig,x#,y#=-22,z#)
		
		If Player_on_floor=True Then
			pxBodySetLocalLinearSpeed(Player_pxBody,0,forcey#,0)
		Else
			pxBodySetLocalLinearSpeed(Player_pxBody,forcex#,forcey#,forcez#)
		EndIf
		
		
		
		
		Go=0
		
		Player_is_walk=False
		If xkht17 Then
			Player_is_walk=True
			If Player_on_floor=True Then
				pxBodySetMyForce(Player_pxBody,0,-1,Player_forceWalkF)
				Go=1
			Else
				If forcez#<Player_forceWalkF/10 Then pxBodySetMyForce(Player_pxBody,0,0,Player_forceWalkF/70)
			EndIf
		EndIf
		If xkht31 Then
			Player_is_walk=True
			If Player_on_floor=True Then
				pxBodySetMyForce(Player_pxBody,0,-1,-Player_forceWalkB)
				Go=1
			Else
				If forcez#>-Player_forceWalkB/10 Then pxBodySetMyForce(Player_pxBody,0,0,-Player_forceWalkB/70)
			EndIf
		EndIf
		If xkht30 Then
			Player_is_walk=True
			If Player_on_floor=True Then
				pxBodySetMyForce(Player_pxBody,-Player_forceWalkS,-1,0)
				Go=1
			Else
				If forcex#>-Player_forceWalkS/10 Then pxBodySetMyForce(Player_pxBody,-Player_forceWalkS/70,0,0)
			EndIf
		EndIf
		If xkht32 Then
			Player_is_walk=True
			If Player_on_floor=True Then
				pxBodySetMyForce(Player_pxBody,Player_forceWalkS,-1,0)
				Go=1
			Else
				If forcex#<Player_forceWalkS/10 Then pxBodySetMyForce(Player_pxBody,Player_forceWalkS/70,0,0)
			EndIf
		EndIf
		
		If xkht57 Then
			If Player_on_floor=True Then
				Player_is_jump=True
				Player_on_floor=False
				pxBodySetMyForce(Player_pxBody,0,Player_forceJump,0)
					;pxBodyAddLocalForce Player_pxBody,0,pxBodyGetMass(Player_pxBody)*2,0,2
			EndIf
		EndIf
		
		If pxTriggerGetNumBody%(Player_legs_trig) ;Sqr(pxContactGetPointNY (Player_pxBody,0)^2) >.70 
			Player_on_floor=True
				;DebugLog "floor"+Rnd(200)
		Else
			Player_on_floor=False
		EndIf
		
		
		If  xKeyDown(29) Then
			If Player_is_duck=False 
				Player_is_duck=True
				
			;pxDeleteBody(Player_pxBody)
				
				
			;Player_temp_modelm=xLoadMesh(PlayerModelPatch$+"player_sit.3DS")  
			;Player_pxBody=BodyCreatehull%(Player_temp_modelm,12);pxBodyCreateCapsule(14,10,8)
			;pxBodySetFrozenRot(Player_pxBody,1)	
			;pxBodySetFlagRayCast (Player_pxBody,0)
			;pxBodySetFlagTriggertable(Player_pxBody, 0)
				
			;Player_pxBodyMat=pxCreateMaterial()								; ?????????? ?????? ??? ?????????? ???????
			;pxMaterialSetDyFriction(Player_pxBodyMat,0.0)					; ???????????? ??????
			;pxMaterialSetRestitution(Player_pxBodyMat,0.0)				; ?????????
			;pxMaterialSetFrictionCombineMode(Player_pxBodyMat,2)		; ??????????????? ??????
			;pxMaterialSetToBody(Player_pxBody,Player_pxBodyMat)
				
			;				pxBodySetRotation(Player_pxBody,0,roty,0)
			;				pxBodySetPosition(Player_pxBody,x#,y#+1,z#)
			;pxBodySetMyForce(Player_pxBody,forcex,forcey,forcez)
			;player_body=Player_pxBody	
			;xFreeEntity Player_temp_modelm
				
			EndIf
		Else
			If Player_is_duck=True 
			;If pxTriggerGetNumBody%(Player_head_trig)=0
			;	pxDeleteBody(Player_pxBody)
				
				
				
			;	Player_temp_modelm=xLoadMesh(PlayerModelPatch$+"player.3DS")   
			;	Player_pxBody=BodyCreatehull%(Player_temp_modelm,12);pxBodyCreateCapsule(14,10,8)
			;	pxBodySetFrozenRot(Player_pxBody,1)	
			;	pxBodySetFlagRayCast (Player_pxBody,0)
			;	pxBodySetFlagTriggertable(Player_pxBody, 0)
				
			;	Player_pxBodyMat=pxCreateMaterial()								; ?????????? ?????? ??? ?????????? ???????
			;	pxMaterialSetDyFriction(Player_pxBodyMat,0.0)					; ???????????? ??????
			;	pxMaterialSetRestitution(Player_pxBodyMat,0.0)				; ?????????
			;	pxMaterialSetFrictionCombineMode(Player_pxBodyMat,2)		; ??????????????? ??????
			;	pxMaterialSetToBody(Player_pxBody,Player_pxBodyMat)
			;	pxBodySetRotation(Player_pxBody,0,roty,0)
			;	pxBodySetPosition(Player_pxBody,x#,y#+1,z#)
			;	pxBodySetMyForce(Player_pxBody,forcex,forcey,forcez)
			;	player_body=Player_pxBody
			;	xFreeEntity Player_temp_modelm
				
				
				
				
				Player_is_duck=False
			;EndIf
			EndIf
		EndIf
		
		
		If  Player_is_duck=True
			If Player_size# >-10
				Player_size#=Player_size#-0.8
				blob=1
			EndIf 
		Else
			If Player_size#<3
				Player_size#=Player_size#+0.8
				blob=1
			EndIf 
		EndIf
		
		
	;If xkht29
	;	If Player_is_duck=False Then
	;		Player_is_duck=True
	;		Player_size#=-10	
	;	EndIf
	;Else
	;	If Player_is_duck=True Then
	;Player_size#=3
;			Player_is_duck=False
;		EndIf
;	EndIf
		
		
		
		xBodySetEntity(Player_model,Player_pxBody)
		FL_Cam=camera
		speed#=1.14
		speed1#=1.44
		
		
		
		mouseyspd#=mouseSpeedY#
		mousexspd#=mouseSpeedX#
		
		
		
		FL_Pitch#=FL_Pitch#-(-(+xJoyRoll()/300+mouseyspd#*0.02)) : FL_Pitch#=FL_Pitch#/1.2
		FL_Yaw#=FL_Yaw#+-((+xJoyZDir()*0.5+mousexspd#*0.02)) : FL_Yaw#=FL_Yaw#/1.2
		
		If updatecam=0
			xMoveMouse (xGraphicsWidth()/2,xGraphicsHeight()/2)
		EndIf 
    ;FL_ZSpeed#=FL_ZSpeed#+Float(xKeyDown(17)-xKeyDown(31))*0.92 : FL_ZSpeed#=FL_ZSpeed#/speed#;  w and s
	;FL_XSpeed#=FL_XSpeed#+Float(xKeyDown(32)-xKeyDown(30))*0.92 : FL_XSpeed#=FL_XSpeed#/speed1# ; a and d
		FL_YSpeed#=0
		FL_Roll#=(FL_Yaw#*1.1)-(FL_XSpeed#*2.3)
		FL_Old_Pitch#=xEntityPitch#(FL_Cam)
		FL_Old_Roll#=xEntityRoll#(FL_Cam)
		xRotateEntity FL_Cam, 0,xEntityYaw#(FL_Cam),0
		xMoveEntity FL_Cam,FL_XSpeed#,0,FL_ZSpeed#
		xRotateEntity FL_Cam, FL_Old_Pitch#,xEntityYaw#(FL_Cam),FL_Old_Roll#
		Local cp#=xEntityPitch(FL_Cam,True)+FL_Pitch#
		If cp<-89 Then cp=-89
		If cp>89 Then cp=89
		
		xRotateEntity FL_Cam,cp,xEntityYaw(FL_Cam)+FL_Yaw#,0
		
		BAGROT=1-BAGROT
		
		pxBodySetRotation(Player_pxBody,0,xEntityYaw#(FL_Cam)+BAGROT,0)
		xPositionEntity FL_cam, pxBodyGetPositionX(Player_pxBody),pxBodyGetPositionY(Player_pxBody)+Player_size#,pxBodyGetPositionZ(Player_pxBody)
		
		
		If Go=1
			PlayerSteps = PlayerSteps + 5*bobpower
			If PlayerSteps>180
				
				MatName$=UpdatePickTexture(Player_model,0,0,-5,0)
			    
				
				If Instr(MatName$,"MEA") Or Instr(MatName$,"META") Or  Instr(MatName$,"")
					SoundMat=1
				EndIf 
				If Instr(MatName$,"EAR") Or Instr(MatName$,"DIRT")
					SoundMat=2
				EndIf 
				If Instr(MatName$,"WOO") Or Instr(MatName$,"PLAN")
					SoundMat=3
				EndIf 
				If Instr(MatName$,"BRI") Or Instr(MatName$,"CON") 
					SoundMat=4
				EndIf 
				
				
				DebugLog SoundMat
				CalcPlayerStep(camera,SoundMat)
				PlayerSteps = 0
			EndIf	
		EndIf
		
		
		If sila#>0.2
			xTranslateEntity camera,Rnd(-sila#,sila#),Rnd(-sila#,sila#),Rnd(-sila#,sila#),0
		Else 
			xTranslateEntity camera,0,0,0,0
		EndIf 
		
		If Player_is_walk=1 And Player_on_floor=1
			blob=1
		EndIf
		
		If blob=1
			a1#=(a1#+camp) Mod 360
			xTurnEntity camera ,0,0,Cos(a1#)*1.1,0
			xMoveEntity camera,Cos(a1#)*0.5,Sin(90+a1#*2)*0.5,0,0
			
			
			
			;xMoveEntity HeadWeapon,Cos(a1#)*0.005,Sin(90+a1#*2)*0.001,0,0
			;xTurnEntity HeadWeapon ,Cos(a1#)*0.18,0,0,0
			
			;xTranslateEntity HeadWeapon,Cos(a1#)*0.64,Sin(90+a1#*2)*0.64,0,0
			;xTurnEntity HeadWeapon ,Cos(a1#)*0.04,0,0,0
			;xMoveEntity HeadWeapon,Cos(a1#)*0.01,Sin(90+a1#*2)*0.01,0,0
			
			
		EndIf 
		
		Mult#=0.4
		V#=(V#+1) Mod 360
		xTurnEntity camera ,0,0,Cos(V#)*Mult#,0
		xMoveEntity camera,Cos(V#)*Mult#,Sin(90+V#*2)*Mult#,0,0
		
		
		
		
		
		
		xMoveMouse xGraphicsWidth() / 2, xGraphicsHeight() / 2
End Function


Function UpdatePickTexture$(EntityAttach,updateTicks=0,dx#=0,dy#=-5,dz#=0, id_tick=0)
	Entity=xLinePick(xEntityX(EntityAttach,1),xEntityY(EntityAttach,1),xEntityZ(EntityAttach,1),dx,dy,dz)	
	If Entity<>0 Then
		SufacePick=xPickedSurface()
		If SufacePick<>0 Then
			Brush=xGetSurfaceBrush(SufacePick)
			If Brush<>0
				SurfPickTexture=xGetBrushTexture(Brush)		
				gNameTexture$=xTextureName(SurfPickTexture)
				
				For i=1 To Len(gNameTexture)
					Char$=Right (gNameTexture,i)
					Char=Left(Char,1)
					If Char="\" Then					
						gNameTexture=Right(gNameTexture,i-1)
					EndIf		
				Next	
				xFreeBrush Brush
				
			;	DebugLog 	gNameTexture$
			EndIf
			
		EndIf
	EndIf
	DebugLog  gNameTexture$
	Return gNameTexture$
End Function




;~IDEal Editor Parameters:
;~C#Blitz3D