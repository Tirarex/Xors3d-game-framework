Include "Inc\Xors3D.bb"
Include "Inc\INI File Functions v2.2.bb"
Include "Inc\FastImageXors.bb"
Include "Inc\SystemFunc.bb"
Include "Inc\sounds.bb"
Include "Deferred\NewDeferred.bb"
Include "Inc\LightEntity.bb"
Include "Inc\Menu.bb"
Include "Inc\PhysXSystem.bb"
Include "Inc\MapSystem.bb"
Include "Inc\Particle.bb"
Include "Inc\Player.bb"

Include "Inc\LevelScripts.bb"

Global EditorEngine=1
Global EditorType,campic



Global controlType   = 0 ;/ 0 - move, 1 - rotate, 2 - scale 
Global selectMask    = 0
Global deltaX#       = 1.0
Global deltaY#       = 1.0
Global deltaZ#       = 1.0

Global controllPosX# = 0.0
Global controllPosY# = 0.0
Global controllPosZ# = 0.0
Global used_controller$ = ""
Global TransController=0 ;0=Move 1=Rotate
Global ShowWindow=1 ;0=nothing 1=Lights 2=PCCM 3=Particles 4=Triggers
Global MoveC=0


Global mousespeed#       = 0.5
Global camerasmoothness# = 4.5

Loadsettings()
LoadStandarts()
xTextureFilter("rai",2)
InitializeDeferred(camera)
LoadUI()
Create_Par()
NoEditorLights=0

nibagEnt=xCreateCube ()


OnInterface=0


ambient =CreateDeferredLight(5,camera)
;DeferredLightColor(ambient,1,1,2)
DeferredLightColor(ambient,5,5,10) ;Chapter_1 DevTest hltest
DirPack$="menu\"
If load=0
	LoadSounds()
	LoadPhysXLib(DataPatch$+"dynamic.ini")
	LoadMapDP(DirPack$,"ololo")
	
EndIf 


Global EntRendCam=xCreateCamera()
xPositionEntity EntRendCam,0,2,-20
xCameraViewport EntRendCam,0,0,160,160
xHideEntity EntRendCam

Global EntImage=xCreateImage(123,123)
Global newcubw=xCreateCube()


Function rendercube(renderent)
	xShowEntity EntRendCam
	xShowEntity renderent
	xPositionEntity EntRendCam,0,2,-(xMeshHeight(renderent)+xMeshWidth(renderent)/2)-5
	xSetBuffer xImageBuffer(EntImage)
	xRenderEntity EntRendCam,renderent
	xSetBuffer xBackBuffer()
	xHideEntity EntRendCam
	xHideEntity renderent
End Function

;MainMenuWindow ID=2
;Buttons id from 1-10

CreateWindow("",0,0,800,56,2)
AddButtonToWindow(" M "  ,8,8,2,1)
AddButtonToWindow(" R ",40,8,2,2)


AddButtonToWindow("    Save    "  ,1,440,2,3,1)
AddButtonToWindow("    Load    ",1,464,2,4,1)

AddButtonToWindow("     Light    "    ,0,288,2,5,1)
AddButtonToWindow("     Phys     "	  ,0,312,2,6,1)
AddButtonToWindow("    PCCM    "	  ,0,336,2,7,1)
AddButtonToWindow("  Triggers  ",0,360,2,8,1)

AddTextToWindow("FPS:",80,5,2,1)
AddTextToWindow("DIP:",120,518,2,2)
AddTextToWindow("Tris:",120,518,2,3)

;Special buttons 40-60
RotationAngleS=90

AddButtonToWindow(" R ",25,216,2,41,1)

AddButtonToWindow("90",1,168,2,42,1)
AddButtonToWindow("45",25,168,2,43,1)
AddButtonToWindow("10",49,168,2,44,1)

AddButtonToWindow(" X ",1,216,2,45,1)
AddButtonToWindow(" X ",49,216,2,46,1)

AddButtonToWindow(" Z ",25,192,2,47,1)
AddButtonToWindow(" Z ",25,240,2,48,1)

AddButtonToWindow(" Y ",1,192,2,49,1)
AddButtonToWindow(" Y ",49,240,2,50,1)



	


;LightControl ID=3
;Buttons id 10-20
CreateWindow("",0,0,111,270,3)

AddButtonToWindow(" Crea "  ,928,8,3,11,1)
AddButtonToWindow(" Del ",968,8,3,12,1)
AddButtonToWindow(" Copy ",1000,8,3,13,1)

NewSlider(3 ,1,928,40,100,0,255,"R:");Red Color
NewSlider(3 ,2,928,72,100,0,255,"G:");Green Color
NewSlider(3 ,3,928,104,100,0,255,"B:");Blue Color
NewSlider(3 ,4,928,136,100,0,10,"Atm:");Atten Multipler

NewSlider(3 ,5,928,168,100,0,1,"Inner:");
NewSlider(3 ,6,928,200,100,0,190,"Outer:");
NewSlider(3 ,7,928,232,100,0,550,"Range:");

NewSlider(3 ,20,928,370,100,0,5,"Int:");
AddSelectorToWindow("Flare", 928,356,3,20)


AddSelectorToWindow("Shadows",928,264,3,1)
AddSelectorToWindow("Spot", 928,288,3,2)
AddSelectorToWindow("Blink",928,312,3,3)
AddSelectorToWindow("Line", 928,336,3,4)
Global LocoR,LocoG,LocoB,LocoAtt#,Inner#,Outer#,LocoShadows=0,LocoSpot=0,LocoRange,LocoBlink


;PhysicControl ID=4
;Buttons id 20-30
CreateWindow("",0,0,111,270,4)

AddButtonToWindow(" Crea "  ,928,8,4,21,1)
AddButtonToWindow(" Del ",968,8,4,22,1)
AddButtonToWindow(" Copy ",1000,8,4,23,1)

AddButtonToWindow("<"  ,920,416,4,24,1)
AddButtonToWindow(">",1031,416,4,25,1)

AddSelectorToWindow("Frozen",928,40,4,5)
AddSelectorToWindow("Breac",928,64,4,6)
NewSlider(4 ,8,928,80,100,0,30000,"Strg:")
AddSelectorToWindow("Door",928,120,4,7)
AddSelectorToWindow("Rootate",928,144,4,8)
NewSlider(4 ,9,928,168,100,0,30000,"Speed:")
AddSelectorToWindow("Lift",928,200,4,9)
AddSelectorToWindow("DoorVert",928,224,4,10)
AddSelectorToWindow("Joint",928,250,4,11)
AddTextToWindow("ID:",36,30,4,4)

;PhysicControl ID=5
;Buttons id 30-40
CreateWindow("",5,0,0,270,5)
AddButtonToWindow(" Crea ",928,8,5,31,1)
AddButtonToWindow(" Del ",968,8,5,32,1)
AddButtonToWindow(" Copy ",1000,8,5,33,1)

AddButtonToWindow(" Update Cube ",928,208,5,34,1)
NewSlider(5 ,10,928,168,100,0,400,"Rad:")

NewSlider(5 ,11,928,40,100,0,200,"SizX:")
NewSlider(5 ,12,928,72,100,0,200,"SizY:")
NewSlider(5 ,13,928,104,100,0,200,"SizZ:")
NewSlider(5 ,14,928,136,100,0,1,"Int:")
SpawnID=1

;Post processing
CreateWindow("",5,70,111,270,6)
NewSlider(6 ,20,5,25,100,1,2,"UnLight:")
NewSlider(6 ,21,5,50,100,1,2,"Contrast:")

Global cube = xCreateCube()
logoTexture = xLoadTexture("logo.jpg")

; texture cube
xEntityTexture cube, logoTexture
xEntityPickMode cube,2
; load instancing shader

GUIImage=xLoadImageEx("EditorGUI.png", 4, FI_AUTOFLAGS)

shader = xLoadFXFile("hwinstancing.fx")

xSetEntityEffect cube, shader
xSetEffectTechnique cube, "NormalMap"

; create cube instances
For x = 0 To 3
	For y = 0 To 3
		For z = 0 To 3
		;	clone = xCreateInstance(cube)
		;	xPositionEntity clone, x * 3, y * 3, z * 3
		Next
	Next
Next



;xCameraViewport(camera,2,20,822,471)

;NE2=CreateEmitter(0,0,0,0) 
;NE2=CreateEmitter(-10,0,0,1) 
;NE2=CreateEmitter(-20,0,0,2) 


For m = 0 To 3
	For i = 0 To 3
	;	clone = xCreateCube()
		;xPositionEntity clone, i * 3, m * 3,  3
		;xEntityTexture clone, logoTexture
		;SetPart(clone)
		;AddAlphaEntity(clone)
	Next
Next

While Not xKeyHit(1) Or xWinMessage("WM_CLOSE")
	Delay(1)
	
	If xKeyHit(8)	xSaveBuffer xTextureBuffer(Droplets),"tex.bmp"
		
		CreateDroplet()
		UpdateDroplets()	
		
		
	If xKeyHit(6) show=1-show
		
		If show=1
			xShowEntity clone
		Else 
			xHideEntity clone
		EndIf  
		
	If GetButtonState(4) 
		DeletePhysXInEditor()
		For L.LightEntity = Each LightEntity
			cube=0
			DeleteAllCubeRef()
			DeleteDeferredLight(l\light)
			DeleteDeferredMesh(l\ControlMesh)
			;xFreeEntity l\ControlMesh
			Delete l
		Next
		LoadLightPatch(0,DirPack$)
		LoadPXMap(DirPack$)
		campic=0
	EndIf 
	
	If GetButtonState(3)  SaveLightPatch(DirPack$) SavePXMap(DirPack$)  SaveReflectionsMap(DirPack$)
		
		If xKeyDown(KEY_TAB)
			updatePhys=1
		Else
			updatePhys=0
		EndIf 
	
	SetWindowText(1,"FPS: "+xGetFPS())
	SetWindowText(2,"DIP: "+xDIPCounter())
	SetWindowText(3,"Tris: "+xTrisRendered())
	
	
	
	If GetButtonState(42)
		RotationAngleS=90
	EndIf
	
	If GetButtonState(43)
		RotationAngleS=45
	EndIf
	
	
	If GetButtonState(44)
		RotationAngleS=10
	EndIf
	
	
	If campic<>0
		If GetButtonState(41)
			xRotateEntity campic,0,0,0
		EndIf 
		
		If GetButtonState(47)
			xTurnEntity campic,RotationAngleS,0,0,True
		EndIf 
		If GetButtonState(48)
			xTurnEntity campic,-RotationAngleS,0,0,True
		EndIf 
		
		If GetButtonState(45)
			xTurnEntity campic,0,-RotationAngleS,0,True
		EndIf 
		If GetButtonState(46)
			xTurnEntity campic,0,RotationAngleS,0,True
		EndIf
		
		
		If GetButtonState(49)
			xTurnEntity campic,0,0,RotationAngleS,True
		EndIf 
		If GetButtonState(50)
			xTurnEntity campic,0,0,-RotationAngleS,True
		EndIf
	EndIf 
	
	
	If GetButtonState(1) Or xKeyHit(2)  TransController=0 
		If GetButtonState(2) Or xKeyHit(3)  TransController=1 
			If GetButtonState(3) Or xKeyHit(4)  TransController=3
				
	
	
			UpdateLightEnt()
			
			
			If updatePhys=0
				For PX.PhysXSystem = Each PhysXSystem
					xEntitySetBody(PX\ObjMesh,PX\ObjBody)
				Next
			Else
				
				UpdatePhysXObj()
			EndIf
			
	
	camspeed#=camspeed#+xMouseZSpeed()
	
	
	; camera control
	If xKeyDown(KEY_W) Then xMoveEntity camera,  0,  0,  camspeed#
	If xKeyDown(KEY_S) Then xMoveEntity camera,  0,  0, -camspeed#
	If xKeyDown(KEY_A) Then xMoveEntity camera, -camspeed#,  0,  0
	If xKeyDown(KEY_D) Then xMoveEntity camera,  camspeed#,  0,  0
	
	Updateipunt()
	msh1=xMouseHit(1)
	
	xSetEffectVector  HDRPoly,"Spex",mouseSpeedX*0.001,mouseSpeedY*0.001,0
	OnInterface=0
	OnInterface=MouseOverlap(0,0,74,490,mX,mY)
	OnInterface=OnInterface+MouseOverlap(921,0,1050,490,mX,mY)
	

	
	
	If GetButtonState(5) Or xKeyHit(KEY_L) ShowWindow=1 campic=0
		If GetButtonState(6) Or xKeyHit(KEY_P) ShowWindow=2 campic=0
			If GetButtonState(7) ShowWindow=3 campic=0
				If GetButtonState(8) ShowWindow=4 campic=0
					
					If msh1 And msd2=0 And OnInterface=0
		campic2=xCameraPick(camera,mX,mY) 
		If campic2<>0
			If  selectMask= 0
				If  ShowWindow = Int(xEntityName(campic2))
					campic=campic2
					
					Select ShowWindow
						Case 1
							LocoR=GetLightR(GetLightFromEnt(campic))
							LocoG=GetLightG(GetLightFromEnt(campic))
							LocoB=GetLightB(GetLightFromEnt(campic))
							LocoAtt#=GetLightAttenMultipler#(GetLightFromEnt(campic))
							Inner#=GetLightInner#(GetLightFromEnt(campic))
							Outer#=GetLightOuter#(GetLightFromEnt(campic))
							LocoShadows=GetLightShadowsS(GetLightFromEnt(campic))
							LocoRange=GetLightRange(GetLightFromEnt(campic))
							LocoSpot=GetLightType(GetLightFromEnt(campic))
							LocoBlink=GetLightBlinkState(campic)
							scatter#=GetScatter#(GetLightFromEnt(campic))
							
							SetValue#(1,LocoR)
							SetValue#(2,LocoG)
							SetValue#(3,LocoB)
							SetValue#(4,LocoAtt#)
							SetValue#(5,Inner)
							SetValue#(6,Outer#)
							SetValue#(7,LocoRange)
							SetSState(1,LocoShadows)
							SetSState(3,LocoBlink)
							SetValue#(20,scatter#)
							
							
							
							If LocoSpot=2
								SetSState(2,1)
							Else 
								If LocoSpot=3
									SetSState(2,1)
									SetSState(4,1)
								Else 
									SetSState(2,0)
								EndIf 
							EndIf 
						Case 2
							PX=GetPhysobjType.PhysXSystem(campic)
							SetSState(5,PX\ObjFrozen)
							SetSState(11,PX\IsJoint)
							SetSState(7,PX\DoorType)
							
							
						Case 3
							
							CD.DeferredReflections=CubeRefGetT.DeferredReflections(campic)
							
							SetValue(10,CD\RefBoxRad)
							SetValue(11,CD\RefBoxXS)
							SetValue(12,CD\RefBoxYS)
							SetValue(13,CD\RefBoxZS)
							SetValue(14,CD\RefIntensity)
							
						Case 4
					End Select
					
					
				EndIf
			EndIf
		EndIf 
	EndIf
	
	If GetButtonState(11) 
		li=CreateControledLight2()
		xPositionEntity li,xEntityX(camera),xEntityY(camera),xEntityZ(camera)
	EndIf 
	
	
	
	
	
	
	
	
	
	If ShowWindow=2
		For PX.PhysXSystem = Each PhysXSystem
			xEntityPickMode PX\ObjMesh,2
		Next
	Else
		For PX.PhysXSystem = Each PhysXSystem
			xEntityPickMode PX\ObjMesh,0
		Next
	EndIf 
	
	If campic<>0 And xKeyHit(KEY_K)
		xEntityPickMode MapMesh ,2
		xCameraPick camera,mX,mY
		xPositionEntity campic, xPickedX(),xPickedY()+1,xPickedZ(),2
		xAlignToVector  campic, xPickedNX(),xPickedNY(),xPickedNZ(),3
		xTurnEntity campic,0,90,0
		xEntityPickMode MapMesh ,0
	EndIf	
	
	
	Select ShowWindow
		Case 0
			
		Case 1
		Case 2
			If GetButtonState(24) SpawnID=SpawnID-1
				If GetButtonState(25) SpawnID=SpawnID+1
					SetWindowText(4,"ID:"+SpawnID)
					If GetButtonState(21) 
						SpawnBody(camera,SpawnID)
					EndIf
					
					If GetButtonState(22) Or xKeyHit(KEY_V)
						DeletePhysOBJ(campic)
						campic=0  
					EndIf
					
					If GetButtonState(23)  
						
						
						PX=GetPhysobjType.PhysXSystem(campic)
						
						campic=SpawnBody(campic,PX\ID,PX\ObjFrozen)
						
					EndIf
					
					
					
					
					
				Case 3
					
					
					If GetButtonState(34)  UpdateCubeRef(campic)
						
						If GetButtonState(31) 
							
							CubeReflectionsCreate(xEntityX(camera),xEntityY(camera),xEntityZ(camera),50,2048)
							
						EndIf 
				Case 4
					
			End Select
			
			
			
	If campic<>0
	Select ShowWindow
		Case 1
			
			If  GetButtonState(12) DeleteLight(campic) campic=0 Or xKeyHit(211)
				If GetButtonState(13) Then campic=CopyLight(campic)
				
				
				
			
			LocoR=GetValue#(1)
			LocoG=GetValue#(2)
			LocoB=GetValue#(3)
			LocoAtt#=GetValue#(4)
			Inner#=GetValue#(5)
			Outer=GetValue#(6)
			LocoRange=GetValue#(7)
			LocoShadows=GetSState(1)
			LocoSpot=GetSState(2)
			LocoBlink=GetSState(3)
			If LocoSpot=1
				If GetSState(4) 
					DeferredLightType(GetLightFromEnt(campic),3)
				Else 
					DeferredLightType(GetLightFromEnt(campic),2)
				EndIf 
			Else
				DeferredLightType(GetLightFromEnt(campic),1)
			EndIf 
			
			scatterpow#=GetValue#(20)
			
			SetScatter(GetLightFromEnt(campic),scatterpow#)
			
			DeferredLightRange(GetLightFromEnt(campic),LocoRange)
			DeferredLightColor(GetLightFromEnt(campic),LocoR,LocoG,LocoB,LocoAtt#)
			DeferredLightShadows(GetLightFromEnt(campic),LocoShadows)
			DeferredLightConeAngles(GetLightFromEnt(campic),Inner#,Outer#)
			DeferredLightBlinkState(campic,LocoBlink)
			
			If  xKeyDown(42) And msh1=1
				campic=CopyLight(campic)
			EndIf 
		Case 2
			PX=GetPhysobjType.PhysXSystem(campic)
			
			PX\ObjFrozen=GetSState(5)
			PX\IsJoint=GetSState(11)
			PX\DoorType=GetSState(7)
		Case 3
			CR.DeferredReflections=CubeRefGetT.DeferredReflections(campic)
			CR\RefBoxRad=GetValue(10)
			CR\RefBoxXS=GetValue(11)
			CR\RefBoxYS=GetValue(12)
			CR\RefBoxZS=GetValue(13)
			
			CR\RefIntensity#=GetValue(14)
			
			
		Case 4
	End Select
EndIf 
	
	If msd2
		mxs# = CurveValue(mouseSpeedX * mousespeed, mxs, camerasmoothness)
		mys# = CurveValue(mouseSpeedY * mousespeed, mys, camerasmoothness)
		camxa# = camxa - mxs Mod 360
		camya# = camya + mys
		If camya < -89 Then camya = -89
		If camya >  89 Then camya =  89
		;xMoveMouse xGraphicsWidth() / 2, xGraphicsHeight() / 2
		xRotateEntity camera, camya, camxa, 0.0
	EndIf 	
	
	UpdateParticles()
	UpdateEmitters()
	If  updatePhys=1
		
		pxRenderPhysic(60,0)
	EndIf 
	
	UpdateFloSmoke()
	RenderWorldDeferred()
	
	xStartDraw
	FIDef()
	If xKeyDown(56)=1
		pxcount=0
		For PX.PhysXSystem = Each PhysXSystem
			xCameraProject camera,xEntityX(PX\ObjMesh),xEntityY(PX\ObjMesh),xEntityZ(PX\ObjMesh)
	;	If xEntityInView(PX\ObjMesh,camera)  
			xSetColor(255, 255, 255)
			xSetScale(1,1)
			xDrawText "Body",xProjectedX(),xProjectedY()-10
			xDrawText "Name:"+PX\ObjName$,xProjectedX(),xProjectedY()-20
			xDrawText "Mass:"+PX\ObjMass,xProjectedX(),xProjectedY()-30
			xDrawText "Material:"+PX\ObjMaterial,xProjectedX(),xProjectedY()-40
	;	EndIf 
			pxcount=pxcount+1
		Next
		
		
		xDrawText "Camera:"+"X"+xEntityX(camera)+";Y"+xEntityY(camera)+";Z"+xEntityZ(camera),10,190	
	EndIf 
	
	
	
	xDrawImageEx GUIImage,1050/2,490/2 
	xEndDraw
	
	
	DrawCubeSize(campic)
	
	
	
	
	
	RenderWindow(2)
	Select ShowWindow
		Case 0
			
		Case 1
			RenderWindow(3)
		Case 2
			If GetLibBody(SpawnID)<>0
				RndrBdy=GetLibBody(SpawnID)
				xTurnEntity RndrBdy,0,0.5,0
				rendercube(RndrBdy)
				xDrawImage EntImage,923,363
			EndIf
			RenderWindow(4)
		Case 3
			RenderWindow(5)
		Case 4
			RenderWindow(6)
	End Select
	
	
	
	
	
	If campic<>0
		UpdateControllers(campic,TransController,msd1)
EndIf 

	nullkeys()
	xFlip
Wend



Type FloSm
	Field Mesh
	Field rot#
	Field RotF#
	Field Alp#
End Type

Function CreateFloSmoke(x#,y#,z#)
	rfs.FloSm = New  FloSm
	rfs\Mesh = xCopyEntity (Particula_Maestra)
	xPositionEntity rfs\Mesh,x,y,z,1
	xEntityTexture rfs\Mesh,xLoadTexture("smoke.png",2+1),0,0
	AddAlphaEntity(rfs\Mesh)
	rfs\Alp#=1
	Size = Rnd(8,16)
	pcolor=Rand(32,64)
	AlphaEntityColor(rfs\Mesh,pcolor,pcolor,pcolor)
	xScaleEntity rfs\Mesh,Size,Size,Size
	rfs\RotF#=Rnd(-0.4,0.4)
End Function




Function UpdateFloSmoke()
	For rfs.FloSm = Each FloSm
		xPointEntity rfs\Mesh,Camera
		rfs\Rot# = rfs\Rot# +rfs\RotF#
		xRotateEntity rfs\Mesh,xEntityPitch(rfs\Mesh,1),xEntityYaw(rfs\Mesh,1),rfs\Rot#,1
		dist#=xEntityDistance( rfs\Mesh,camera)/40
		If dist#>1
			dist#=1
		EndIf 
;		rfs\Alp#=rfs\Alp#-0.005
		AlphaEntityAlpha(rfs\Mesh,dist#)
		
	Next
End Function


Function UpdateControllers(cube,controlType,mosedown)
	If cube <>0
		x# = xEntityX(cube)
		y# = xEntityY(cube)
		z# = xEntityZ(cube)
		Select controlType
			Case 0
				mask = xCheckMovementGizmo(x#, y#, z#, camera, mX, mY)
				If Not mosedown
					selectMask   = mask
					controllPosX = x
					controllPosY = y
					controllPosZ = z
				EndIf
				xDrawMovementGizmo(x#, y#, z#, selectMask)
				used_controller$ = "Used move controler"
			Case 1
				mask = xCheckRotationGizmo(x#, y#, z#, camera, mX, mY)
				If Not mosedown
					selectMask   = mask
					controllPosX = x
					controllPosY = y
					controllPosZ = z
					deltaX#      = 0.0
					deltaY#      = 0.0
					deltaZ#      = 0.0
				EndIf
				xDrawRotationGizmo(x#, y#, z#, selectMask, deltaX, deltaY, deltaZ)
				used_controller$ = "Used rotate controler"
				
			Default
				used_controller$ = ""
		End Select
		MoveC=0
		If  cube<>0
	; object control
			If mosedown And selectMask <> 0
				useX     = (selectMask Shr 0) And 1
				useY     = (selectMask Shr 1) And 1
				useZ     = (selectMask Shr 2) And 1
				useG     = (selectMask Shr 3) And 1
				factorX# = 0.7 / Float(xGraphicsWidth())
				factorY# = 0.7 / Float(xGraphicsHeight())
				Select controlType
		; if movement gizmo is used
					Case 0
			; move controlled entity
						dx#   = controllPosX - xEntityX(camera, True)
						dy#   = controllPosY - xEntityY(camera, True)
						dz#   = controllPosZ - xEntityZ(camera, True)
						dist# = Sqr(dx * dx + dy * dy + dz * dz)
			; x-axis
						If useX
							move# = ComputeMove(camera, 10.0, 0.0, 0.0) * factorX * dist
							xTranslateEntity cube, move, 0.0, 0.0, False
							MoveC=1
						EndIf
			; y-axis
						If useY
							move# = ComputeMove(camera, 0.0, 10.0, 0.0) * factorY * dist
							xTranslateEntity cube, 0.0, move, 0.0, False
							MoveC=1
						EndIf
			; z-axis
						If useZ
							move# = ComputeMove(camera, 0.0, 0.0, 10.0) * factorX * dist
							xTranslateEntity cube, 0.0, 0.0, move, False
							MoveC1=1
						EndIf
		;
		; if rotation gizmo is used
					Case 1
			; rotate controlled entity
			; x-axis
						If useX
							move#  = ComputeMove(camera, 0.0, -10.0, 0.0)
							deltaX = deltaX + move
							xTurnEntity cube, move, 0.0, 0.0, True
						EndIf
			; y-axis
						If useY
							move#  = ComputeMove(camera, -10.0, -10.0, 0.0);
							deltaY = deltaY + move
							xTurnEntity cube, 0.0, move, 0.0, True
						EndIf
			; z-axis
						If useZ
							move#  = ComputeMove(camera, -10.0, 0.0, 0.0);
							deltaZ = deltaZ + move
							xTurnEntity cube, 0.0, 0.0, move, True
						EndIf
				End Select
			EndIf
		EndIf
	EndIf 
End Function
Function ComputeMove#(camera%, x#, y#, z#)
	If mouseSpeedX = 0 And mouseSpeedY = 0 Then Return 0.0
	; project axis on the screen
	xCameraProject(camera, controllPosX, controllPosY, controllPosZ)
	x1 = xProjectedX()
	y1 = xProjectedY()
	xCameraProject(camera, controllPosX + x, controllPosY + y, controllPosZ + z)
	x2 = xProjectedX()
	y2 = xProjectedY()
	; compute angle between our vectors
	dx1    = x2 - x1
	dy1    = y2 - y1
	dx2    = mouseSpeedX*2
	dy2    = mouseSpeedY*2
	len1#  = Sqr(dx1 * dx1 + dy1 * dy1)
	len2#  = Sqr(dx2 * dx2 + dy2 * dy2)
	angle# = ACos(Float(dx1 * dx2 + dy1 * dy2) / (len1 * len2))
	; compute distance
	radii# = Sqr(dx2 * dx2 + dy2 * dy2)
	; compute a new vector's x-component
	Return radii * Cos(angle)
End Function


;~IDEal Editor Parameters:
;~C#Blitz3D