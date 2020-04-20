Global ParticleMesh 
Function Create_Par()
	ParticleMesh = xCreateMesh()
	Surface = xCreateSurface(ParticleMesh)
	xAddVertex Surface, -1, 1, 0, 0, 0
	xAddVertex Surface,  1, 1, 0, 1, 0
	xAddVertex Surface, -1,-1, 0, 0, 1
	xAddVertex Surface,  1,-1, 0, 1, 1
	xAddTriangle Surface, 0, 1, 2
	xAddTriangle Surface, 3, 2, 1
	xFlipMesh ParticleMesh
	xUpdateNormals ParticleMesh
	xPositionEntity ParticleMesh,0,0,0
	xHideEntity ParticleMesh
End Function






Function LoadParticleSystem(ParticleInIPatch$)
	DebugLog "Particle:" +ParticleInIPatch$
	
	INI_OpenFile2(ParticleInIPatch$)
	SoundsCount = INI_ReadValue("Particle", "SoundsCount", "0") 
	For SC=1 To SoundsCount
		SPoint=SPoint+1
		
		
		ParticleX=    INI_ReadValue("Particle_"+SPoint, "ParticleX", "0")
		ParticleY=    INI_ReadValue("Particle_"+SPoint, "ParticleY", "0")
		ParticleZ=    INI_ReadValue("Particle_"+SPoint, "ParticleZ", "0")
		
		
		ParticleRoll=    INI_ReadValue("Particle_"+SPoint, "ParticleRoll", "0")
		ParticleYaw=    INI_ReadValue("Particle_"+SPoint, "ParticleYaw", "0")
		ParticlePitch=    INI_ReadValue("Particle_"+SPoint, "ParticlePitch", "0")
		
		NE=CreateEmitter(ParticleX,ParticleY,ParticleZ,0) 
		xRotateEntity(NE,ParticleRoll, ParticleYaw, ParticlePitch)
		
		
	Next	
	INI_CloseFile%()
	
End Function







Type Particles
	Field obj%, pvt%
	Field image%
	
	Field R#, G#, B#, A#, size#
	Field speed#, yspeed#, gravity#
	Field Rchange#, Gchange#, Bchange#, Achange#
	Field SizeChange#
	Field Dealph#
	Field lifetime#
End Type 

Function CreateParticle.Particles(x#, y#, z#, image%, size#, gravity# = 1.0, lifetime% = 200,Dealph#=0.01)
	Local p.Particles = New Particles
	p\lifetime = lifetime
	p\Dealph#=Dealph#
	p\obj = xCopyEntity (ParticleMesh)
	xPositionEntity(p\obj, x, y, z, True)
	xEntityTexture(p\obj, SmokeTex)
	xRotateEntity(p\obj, 0, 0, Rnd(0,360))
	xEntityFX(p\obj, 1 + 8)
	AddAlphaEntity(p\obj)
	Select image
		Case 0, 5, 6
			;xEntityBlend(p\obj, 1)
		Case 1,2,3,4
			;xEntityBlend(p\obj, BLEND_ADD)
	End Select
	
	p\pvt = xCreatePivot()
	xPositionEntity(p\pvt, x, y, z, True)
	
	p\image = image
	p\gravity = gravity * 0.004
	p\R = 255 : p\G = 255 : p\B = 255 : p\A = 1.0
	p\size = size
	xScaleEntity (p\obj, p\size, p\size, p\size)
	Return p
End Function

Function UpdateParticles()
	For p.Particles = Each Particles
		xMoveEntity(p\pvt, 0, p\speed , 0)
		If p\gravity <> 0 Then p\yspeed = p\yspeed - p\gravity 
		xTranslateEntity(p\pvt, 0, p\yspeed , 0)
		xPointEntity p\obj,Camera
		xPositionEntity(p\obj, xEntityX(p\pvt,True), xEntityY(p\pvt,True), xEntityZ(p\pvt,True), True)
		
		;xTurnEntity(p\obj, 0, 0, FPSfactor)
		
		If p\Achange <> 0 Then
			;xEntityAlpha(p\obj, p\A)	
			alphaEntityAlpha(p\obj, p\A)
			;p\A=Min(Max(p\a+p\Achange ,0.0),1.0)
			p\A=p\A-p\Dealph#
		EndIf
		
		If p\sizechange <> 0 Then 
			p\size= p\size+p\SizeChange 
			xScaleEntity (p\obj, p\size, p\size, p\size)
		EndIf
		p\lifetime=p\lifetime-1
		If p\lifetime <= 0 Or p\size < 0.00001 Or p\a =< 0 Then
			RemoveParticle(p)
		End If
	Next
End Function



Function RemoveParticle(p.Particles)
	DeleteAlphaEntity(p\obj)
	xFreeEntity(p\pvt)	
	Delete p
End Function












Type Emitters
	Field Obj%
	
	Field Size#
	Field MinImage%, MaxImage%
	Field Gravity#
	Field LifeTime%
	Field Dealph#
	
	Field Disable%
	Field SpawnTimer
	Field SpawnMax
	
	Field SoundCHN%
	
	Field Speed#, RandAngle#
	Field SizeChange#, Achange#
End Type 


Function UpdateEmitters()
	For e.emitters = Each Emitters
		e\SpawnTimer=e\SpawnTimer+1
		If e\SpawnTimer>e\SpawnMax
			e\SpawnTimer=0
			p.Particles = CreateParticle(xEntityX(e\obj, True), xEntityY(e\obj, True), xEntityZ(e\obj, True), Rand(e\minimage, e\maximage), e\size, e\gravity, e\lifetime,e\Dealph#)
			p\speed = e\speed
			xRotateEntity(p\pvt, xEntityPitch(e\Obj, True), xEntityYaw(e\Obj, True) , xEntityRoll(e\Obj, True) , True)
			xTurnEntity(p\pvt, Rnd(-e\RandAngle, e\RandAngle), Rnd(-e\RandAngle, e\RandAngle), 0)
			p\SizeChange = e\SizeChange
			p\Achange = e\achange
		EndIf 
		
		
		
		
		
	Next
End Function 


Function CreateEmitter(x#, y#, z#, emittertype%) 
	Local e.Emitters = New Emitters
	
	e\Obj = xCreatePivot()
	xPositionEntity(e\Obj, x, y, z, True)
	
	Select emittertype
		Case 0 ;savu
			e\Size = 1.6
			e\Gravity = -.4
			e\LifeTime = 800
			e\SizeChange = 0.10
			e\Speed = 0.18
			e\RandAngle = 20
			e\Achange = -0.16
			e\SpawnMax=5
			e\Dealph#=0.01
		Case 1
			e\Size = 0.6
			e\Gravity = -0.2
			e\LifeTime = 200
			e\SizeChange = 0.08
			e\Speed = 0.2
			e\RandAngle = 10
			e\Achange = -0.01
			e\SpawnMax=5
			e\MinImage = 6 : e\MaxImage = 6
			e\Dealph#=0.01
			
		Case 2 ;savu
			e\Size = 8.6
			e\Gravity = 0
			e\LifeTime = 1800
			e\SizeChange = 0.001
			e\Speed = 0.08
			e\RandAngle = 5
			e\Achange = -0.06
			e\SpawnMax=50
			e\Dealph#=0.002
	End Select
	Return e\Obj
End Function




;~IDEal Editor Parameters:
;~C#Blitz3D