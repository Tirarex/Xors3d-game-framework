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
;Include "Inc\LevelScripts.bb"

Loadsettings()
LoadStandarts()
Status_bar(10)
Status_bar(10)
LoadUI()
Status_bar(10)
PostSSAO=0
InitializeDeferred(camera)
Status_bar(20)
LoadSounds()
Create_Par()
Status_bar(20)
;Global NOBAGCUBESUKA=xCreateCube
xCreateCube
ReadDistructDir(GlobalDirectoru$+GameFolder$+"Destruction")

LoadPhysXLib(DataPatch$+"dynamic.ini")
Status_bar(20)

Global BlurTexSize=512
Global poly = xCreatePostEffectPoly(Camera, 1)
Global tex  = xCreateTexture(BlurTexSize, BlurTexSize)
Global tex2 = xCreateTexture(ScreenW, ScreenH) 
shader = xLoadFXFile("Deferred\DeferredHDR.fx")
noise = xLoadTexture ("Deferred\textures\noise.png")
LensTexture = xLoadTexture ("Deferred\textures\lensdirt.png")
xSetEntityEffect poly, shader
xSetEffectTechnique poly, "HDR"
xSetEffectMatrixSemantic poly, "MatWorldViewProj", WORLDVIEWPROJ
xSetEffectTexture poly, "tDiffuse", tex
xSetEffectTexture poly, "tEmissive", tex2
xSetEffectTexture poly,  "noiseTexture", noise
xSetEffectTexture poly,  "LensTexture",LensTexture
;xSetEffectTexture poly, "tBright", gGBTex2

;ambient =CreateDeferredLight(5,camera)
;DeferredLightColor(ambient,1,1,2)
;DeferredLightColor(ambient,14,14,29)
;DeferredLightColor(ambient,80,80,100)


Global DropletBuff=CreateDropletBuffer()
xSetEffectTexture (poly, "DiTex", DropletBuff)


AnimShader = xLoadFXFile("Deferred\skinning.fx")




Global HeadWeapon=xLoadAnimMesh("Weapons\h_ppk.b3d",camera)
xPositionEntity HeadWeapon,0.3,0,-1.2
xScaleEntity  HeadWeapon,0.1,0.1,0.1
xRotateEntity HeadWeapon,0,-180,0
;xEntityOrder HeadWeapon,-1
xSetFrustumSphere(HeadWeapon, 0,0,0, 1000)
xSetEntityEffect HeadWeapon, AnimShader
xSetBonesArrayName HeadWeapon, "bonesMatrixArray"
xSetEffectTechnique HeadWeapon, "Skinned"



xExtractAnimSeq(HeadWeapon, 1, 31)
Drawanim1=1

xExtractAnimSeq(HeadWeapon, 31, 67)
Drawanim2=2

xExtractAnimSeq(HeadWeapon, 67, 162)
Drawanim3=3


xAnimate HeadWeapon,0,1,2



ShootSound=xLoad3DSound("Weapons\pm_shoot.ogg")
ReloadSound=xLoad3DSound("Weapons\pm_reload.ogg")
Global DrawSound=xLoad3DSound("Weapons\generic_draw.ogg")





Global WeaponShow=0
Global WeaponShowAnim=0Global AmmoCount=8
Global WeaponLightTimer=0
Global HideTimer=0

WeaponLight=CreateDeferredLight(1,HeadWeapon)
DeferredLightColor(WeaponLight,256, 255, 0)
DeferredLightRange(WeaponLight,190)
DeferredLightShadows(WeaponLight,0)


Global DebrisTex=xLoadTexture(TexturesPatch$+"Debris1.png",2)
Global PlayerHaveWeapon=1

;CreatenDoor(0)
;CreatenDoor(1)


Global Keyp

While Not ExitYes
	
	
	
	
	
	msh1=xMouseHit(1)
	
	Keyp= xKeyHit (KEY_P) 
	
	
	
	
	
		
	
	If xKeyHit (KEY_U)
		NE=CreateEmitter(0,10,0,0) 
		
		xRotateEntity(NE,xEntityPitch (camera,1), xEntityYaw (camera,1), xEntityRoll (camera,1))
		xPositionEntity(NE,xEntityX(camera,1),xEntityY(camera,1),xEntityZ(camera,1))
		
	EndIf 
	
	If xKeyHit (KEY_g)
		
		CubeReflectionsCreate(xEntityX(camera,1),xEntityY(camera,1),xEntityZ(camera,1),90,128)
		
	EndIf 
	
	
	Repeat 
		frameElapsed = xMillisecs () - frameTime 
	Until frameElapsed 
	frameTicks = frameElapsed / framePeriod 
	frameTween# = Float (frameElapsed Mod framePeriod) / Float (framePeriod) 
	For frameLimit = 1 To frameTicks 
		If frameLimit = frameTicks Then xCaptureWorld 
		frameTime = frameTime + framePeriod
		Select gamestate
			Case 0 
				mX = (xMouseX() - Float(xGraphicsWidth()) * 0.5) * 0.05
				mY = -(xMouseY() - Float(xGraphicsHeight()) * 0.5) * 0.05
				xRotateEntity(camera, -mY * 0.25, -mX * 0.25, 0)
				Updateipunt()
			Case 2
				If xKeyHit(KEY_V) ObjLiftState=1-ObjLiftState
				
				
					Updateipunt()
					
					If xKeyHit(KEY_B) RenderCubeReflection(EnvCube, camera)
				
				

				
					If WeaponShowAnim=1 And WeaponShow=1    And HideTimer=0
						WeaponShow=0
						WeaponShowAnim=0
						xPositionEntity HeadWeapon,0.3,0,-100.2
					EndIf 
					keym=xKeyHit(KEY_Q)
					If keym And PlayerHaveWeapon=1
						If WeaponShow=0
							If	WeaponShowAnim=0
								ShowWeapon()
								keym=0
							EndIf
						EndIf 
					EndIf 
					
					
					
					If HideTimer>0 HideTimer=HideTimer-1
						
						If WeaponShowAnim=1 And WeaponShow=0 And xAnimSeq ( HeadWeapon )=-1
							WeaponShow=1
							WeaponShowAnim=0
							
						EndIf 
						
						
						If keym
							If WeaponShow=1
								If	WeaponShowAnim=0
									HideWeapon()
								EndIf
							EndIf 
						EndIf 
						
						
						
						
						If 	msh1=1
							If WeaponShow=1  And AmmoCount>0 And   xAnimSeq ( HeadWeapon )=-1
								xAnimate HeadWeapon,3,10,2
								xEmitSound( ShootSound,camera)
								
								Shooot(camera,0.05)
								AmmoCount=AmmoCount-1
								
								WeaponLightTimer=5
								
							EndIf
						EndIf
				DeferredLightState(WeaponLight,0)
				If WeaponLightTimer>0
					WeaponLightTimer=WeaponLightTimer-1
					DeferredLightState(WeaponLight,1)
					
				EndIf 
				
				
				If 	xkht42
					If WeaponShow=1 And xAnimSeq ( HeadWeapon )=-1 And AmmoCount<8
						xAnimate HeadWeapon,3,2,3
						AmmoCount=8
						xEmitSound( ReloadSound,camera)
						
					EndIf
				EndIf
				
				
				
				UpdateLogic()
			;	If WeaponShow=0
					BodyControl()
			;	EndIf 
				If WeaponShow=1 And ForHandBody<>0 Then
					pxBodySetLinearDamping(ForHandBody,0)
					;pxBodySetAngularSpeed(ForHandBody ,0,0,0)
					;pxBodySetLocalLinearSpeed(ForHandBody ,0,0,0)
					ForHandBody=0
				EndIf 
				Update_bullets()
				UpdateLightEnt()
				UpdatePhysXObj()
				;Update_Par()
				;UpdateLevelScripts()
				UpdateDoor(camera)
				UpdatenDoor()
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
		If gamestate=2
		xUpdateWorld()
	EndIf 
		
		
		DeferredTime=MilliSecs ()
		RenderWorldDeferred()
		DeferredTime=MilliSecs ()-DeferredTime
	 
		;If psdfgh=0
	
		xStretchBackBuffer(tex2, 0, 0, ScreenW, ScreenH, 0)
		xSetEffectTechnique(poly, "DS")
		xRenderPostEffect(poly)
		
		xStretchBackBuffer(tex, 0, 0, BlurTexSize, BlurTexSize, 0)
		xSetEffectTechnique(poly, "DiffuseV")
		xRenderPostEffect(poly)
		
		xStretchBackBuffer(tex, 0, 0, BlurTexSize, BlurTexSize, 0)
		xSetEffectTechnique(poly, "DiffuseH")
		xRenderPostEffect(poly)
		xStretchBackBuffer(tex, 0, 0, BlurTexSize, BlurTexSize, 0)
		
		xSetEffectFloat poly, "RandomValue", Rnd(0,10)
		xSetEffectTechnique(poly, "HDR")
		xRenderPostEffect(poly)
		
		;EndIf 
		
		
		
		If ShowMaps=1
			RenderWindow(3)
		EndIf
		
		If ShowMenu=1
			RenderWindow(2)
		EndIf
		
		;DrawGui()
		Draw()
		
		nullkeys()
		xFlip() 	
	Wend
	
	
	
	
;~IDEal Editor Parameters:
;~C#Blitz3D