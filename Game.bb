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

Loadsettings()
LoadStandarts()
Status_bar(10)
Status_bar(10)
LoadUI()
Status_bar(10)

InitializeDeferred(camera)

Status_bar(20)
LoadSounds()
Create_Par()
Status_bar(20)
Global NOBAGCUBESUKA=xCreatePivot

ReadDistructDir(GlobalDirectoru$+GameFolder$+"Destruction")

Status_bar(20)


LoadMenuBackuground()

LoadPhysXLib(DataPatch$+"dynamic.ini")
Global Keyp
PostSSAO=0
water= pxCreateWaterInfinPlane(150)
While Not ExitYes
	PostSSAO=0
	;xTurnEntity NE,1,0,0
	Delay(1)
	
	
	msh1=xMouseHit(1)
	
	Keyp= xKeyHit (KEY_P) 
	
	
	Repeat 
		frameElapsed = xMillisecs () - frameTime 
	Until frameElapsed 
	frameTicks = frameElapsed / framePeriod 
	frameTween# = Float (frameElapsed Mod framePeriod) / Float (framePeriod) 
	For frameLimit = 1 To frameTicks 
		;If frameLimit = frameTicks Then xCaptureWorld 
		frameTime = frameTime + framePeriod
		Select gamestate
			Case 0 
				
				
				V#=(V#+1) Mod 360
				xTurnEntity camera ,0,0,Cos(V#)*0.01,0
				xMoveEntity camera,Cos(V#)*0.01,Sin(90+V#*2)*0.01,0,0
				
				
				Updateipunt()
				UpdateLightEnt()
			Case 2
				If 	UpdateReflections=0
					UpdateReflections=1
					 UpdateAllCubeRef()
				EndIf 
				Updateipunt()
				campic=xCameraPick(camera,xMouseX(),xMouseY())
				If xMousedown(3)
					
					
					lightg=CreateDeferredLight(1)
					xPositionEntity lightg,xPickedX(),xPickedY(),xPickedZ()
					DeferredLightColor(lightg,Rand(0,255), Rand(0,255), Rand(0,255))
					DeferredLightRange(lightg,Rand(15,60))
					
					
					xPointEntity lightg,camera
					xMoveEntity lightg,0,0,8,0
					
				EndIf
				
				If xKeyHit(KEY_o) PauseLiz=1-PauseLiz
				
				xUpdateWorld()
				UpdateLogic()
				BodyControl()
				UpdateLightEnt()
				UpdatePhysXObj()
				UpdateScripts()
				UpdateSoundSystem()
				;Update_Par()
				;UpdateChapter_1()
				UpdateDoor(camera)
				UpdateParticles()
				UpdateEmitters()
				
				
				PhysicTime=MilliSecs ()
				pxUpdateTriggers()
				
				pxRenderPhysic(30,0)
				
				PhysicTime=MilliSecs ()-PhysicTime
				
				Case 3
					
					
					
			End Select
		Next	
		
		;CreateDroplet() 
		
		;UpdateDroplets()
		PostSSAO=1
		DeferredTime=MilliSecs ()
		RenderWorldDeferred()
		;DrawEYEAdp ()
		;xRenderWorld
		DeferredTime=MilliSecs ()-DeferredTime
		
		
		
		;DrawGui()
		Draw()
		
		nullkeys()
		xFlip() 	
	Wend
	
;~IDEal Editor Parameters:
;~C#Blitz3D