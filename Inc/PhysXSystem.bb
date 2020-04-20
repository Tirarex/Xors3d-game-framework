Type PhysLib
	Field mesh
	Field body
	Field name$
	Field mass
	Field material
	Field Id
End Type
Global physCount

Function SpawnBody(mesh,Id,froz=0)
	For pl.PhysLib = Each PhysLib
		If Id=pl\Id
			ObjBody=SetMeshAsPhysXObj2(pl\mesh,pl\body,pl\mass,pl\material,1,pl\name$,pl\Id,froz)
			xPositionEntity ObjBody,xEntityX(mesh),xEntityY(mesh),xEntityZ(mesh)
			xRotateEntity(ObjBody,xEntityPitch(mesh),xEntityYaw(mesh),xEntityRoll(mesh))
			Return ObjBody
		EndIf
	Next
End Function

Function GetLibBody(id)
	For pl.PhysLib = Each PhysLib
		If id=pl\Id
			Return pl\mesh
		EndIf
	Next
End Function


Function LoadPhysXLib(patch$)
	INI_OpenFile2(patch$)
	physCount = INI_ReadValue("PhysObj", "Count", "0") 
	For lf=1 To physCount
		PoCP=PoCP+1
		Mesh$= 		INI_ReadValue("Po_"+PoCP, "Mesh", "0")
		Body$= 		INI_ReadValue("Po_"+PoCP, "Body", "0")
		Name$= 		INI_ReadValue("Po_"+PoCP, "Name", "0")
		Material= 	INI_ReadValue("Po_"+PoCP, "Material", "0")
		Mass#= 		INI_ReadValue("Po_"+PoCP, "Mass", "0")
		AddBodyToLib(Mesh$,Name$,Mass#,Material,PoCP)
	Next	
	INI_CloseFile%()
End Function


Function GetBodyName$(Id)
	For pl.PhysLib = Each PhysLib
		If Id=pl\Id
			Return  pl\name$
		EndIf
	Next
End Function


Function AddBodyToLib(mesh$,name$,mass,material,Id)
	pl.PhysLib = New PhysLib
	pl\name$=name$
	pl\Id=Id
	pl\mesh = xLoadMesh(DataPatch$+"phys\"+mesh$)
	xPositionEntity pl\mesh,0,0,0
	xRotateEntity pl\mesh,0,0,0
	pl\body = BodyCreateHull%(pl\mesh, mass)
	pxBodySetFrozen pl\body,1
	pxBodySetFlagCollision(pl\body, 0)
	pxBodySetFlagRayCast (pl\body,0)
	pxBodySetFlagTriggertable(pl\body, 0)
	pl\material=material
	pl\mass=mass
	xHideEntity pl\mesh 
End Function






Type PhysXSystem
	;Main
	Field ObjBody
	Field ObjMesh
	Field ObjKep
	Field ID
	Field ObjStatic
	Field IsTrimesh
	
	;Other
	Field ObjMass
	Field ObjName$
	Field ObjIsExplosive
	Field ObjFrozen
	Field ObjMaterial
	Field ObjSoundTimer
	Field ObjSoundLastTime
	Field ObjContactSoundForce
	Field DoorType
	
	
	;Joint
	Field IsJoint
	Field jointH
	
	;Destruction
	Field ObjDestructMode
	Field ObjDestructName
	
	;Kinematic
	Field ObjIsKinematic
	Field ObjKinematicType
	Field ObjHasRotating
	Field ObjRotatingSpeed
	Field ObjRoot
	
	Field ObjLiftY#
	Field ObjLiftActivity
	Field ObjLiftState
	
End Type

Function SetObjKinematic(NewKineObj,FFobj,KineType=1,KineSpeed=10)
	For PX.PhysXSystem = Each PhysXSystem
		If PX\ObjMesh=NewKineObj Or PX\ObjBody=NewKineObj
			
			
			PX\ObjLiftY=xEntityY(FFobj,1)
			DebugLog PX\ObjLiftY
			pxDeleteBody PX\ObjBody
			PX\IsTrimesh=1
			
			PX\ObjBody=BodyCreateMesh(PX\ObjMesh,0)
			;pxBodySetPosition(PX\ObjBody,xEntityX(PX\ObjMesh),xEntityY(PX\ObjMesh),xEntityZ(PX\ObjMesh))
			
			pxBodySetPosition PX\ObjBody ,xEntityX(FFobj,1),xEntityY(FFobj,1),xEntityZ(FFobj,1)
			pxBodySetRotation (PX\ObjBody, xEntityPitch (FFobj,1), xEntityYaw (FFobj,1), xEntityRoll (FFobj,1))
			
			;pxBodySetRotation (PX\ObjBody, 0,0,0)
			
			
			pxKinematicSet(PX\ObjBody)
			
		
			;pxKinematicSetPosition(PX\ObjBody,xEntityX(PX\ObjMesh),xEntityY(PX\ObjMesh),xEntityZ(PX\ObjMesh))
			PX\ObjIsKinematic=1
			PX\ObjKinematicType=KineType
		PX\ObjHasRotating=1
		PX\ObjRotatingSpeed=KineSpeed
		
		
		
		
		EndIf	
	Next 
End Function


Function DeletePhysXObj(MBForDel)
	For PX.PhysXSystem = Each PhysXSystem
		If PX\ObjMesh=MBForDel Or PX\ObjBody=MBForDel
			
			pxBodySetFlagCollision(PX\ObjBody, 0)
			xHideEntity PX\ObjMesh 
			
		EndIf 
	Next 
End Function

Function DeletePhysXInEditor()
	For PX.PhysXSystem = Each PhysXSystem
		
			xHideEntity PX\ObjMesh 
			pxDeleteBody PX\ObjBody
			Delete PX
	Next 
End Function

Function DeletePhysOBJ(MBForDel)
For PX.PhysXSystem = Each PhysXSystem
	If PX\ObjMesh=MBForDel Or PX\ObjBody=MBForDel
		
	xHideEntity PX\ObjMesh 
	pxDeleteBody PX\ObjBody
	Delete PX
EndIf
Next 
End Function

Function GetPhysobjType.PhysXSystem(MBForDel)
	For PX.PhysXSystem = Each PhysXSystem
		If PX\ObjMesh=MBForDel Or PX\ObjBody=MBForDel
			
			Return PX
			
		EndIf 
	Next 
End Function

Function DeleteAllPhysXObj()
	For PX.PhysXSystem = Each PhysXSystem
		
			xFreeEntity PX\ObjMesh
			pxDeleteBody PX\ObjBody
			Delete PX
			
		Next 
		
		For db.DestructBody = Each DestructBody
			xFreeEntity db\mesh
			pxDeleteBody db\body
			Delete db
		Next
End Function


Function CreateMeshAsPhysXObj(MeshForPx,Mass#=10,ObjName$="",KepCretae=1,ObjMaterial=1)
	If MeshForPx<>0
		;UpdatePhysXObj()
		
		PX.PhysXSystem = New PhysXSystem
		PX\ObjMesh=xCopyEntity(MeshForPx)
		xPositionEntity PX\ObjMesh,0,0,0
		;lig =DCreateLight(0,0,0,90,255,255,255,2,0)
		;xTurnEntity lig,90,0,0
		;xEntityParent(lig,PX\ObjMesh,1)
		;AddDeferredReciver(PX\ObjMesh,1)
		SetDeferredMesh(PX\ObjMesh,1)
		
		xNameEntity PX\ObjMesh,"2"
		xEntityPickMode PX\ObjMesh,1
		
		PX\ObjBody=BodyCreateHull(MeshForPx,Mass#) 
		pxBodySetFlagRayCast (PX\ObjBody,1)
		pxBodySetFlagTriggertable(PX\ObjBody, 1)
		
		PX\ObjMaterial=ObjMaterial
		PX\ObjName$=ObjName$
		PX\ObjMass=Mass#
		PX\ObjKep=AddWaterBodyToPhysObj(PX\ObjBody)
		Return PX\ObjBody
	EndIf 
End Function

Function GetMeshFromBody(Body)
	For PX.PhysXSystem = Each PhysXSystem
		If Body=PX\ObjBody
			Return  PX\ObjMesh
		EndIf
	Next
End Function


Function GetBodyFromMesh(Mesh)
	For PX.PhysXSystem = Each PhysXSystem
		If Mesh=  PX\ObjMesh
			Return	PX\ObjBody	
		EndIf
	Next
End Function

Function DoorType(mesh,dtype)
	For PX.PhysXSystem = Each PhysXSystem
		If PX\ObjMesh=mesh
			PX\DoorType=dtype
			Exit 
		EndIf 
    Next
End Function

Function  SetJointBody(Mesh)
	For PX.PhysXSystem = Each PhysXSystem
		If Mesh=  PX\ObjMesh
			PX\IsJoint=1	
		EndIf
	Next
End Function


Function AddMeshPhysXObj(MeshForPx,Mass#=10,ObjMaterial=1,KepCretae=1,ObjName$="")
	If MeshForPx<>0
		PX.PhysXSystem = New PhysXSystem
		PX\ObjMesh=MeshForPx
		;xPositionEntity PX\ObjMesh,0,0,0
		;lig =DCreateLight(0,0,0,90,255,255,255,2,0)
		;xTurnEntity lig,90,0,0
		;xEntityParent(lig,PX\ObjMesh,1)
		;SetDeferredMesh(PX\ObjMesh,1)
		;PX\ObjBody=BodyCreateHull(PX\ObjMesh,1) 
		PX\ObjBody=pxBodyCreateCube(1,1,1,1)
		pxBodySetFlagRayCast (PX\ObjBody,1)
		pxBodySetFlagTriggertable(PX\ObjBody, 1)
		
		xNameEntity PX\ObjMesh,"2"
		xEntityPickMode PX\ObjMesh,1
		PX\ObjMaterial=ObjMaterial
		PX\ObjName$=ObjName$
		PX\ObjMass=Mass#
	;	PX\ObjKep=AddWaterBodyToPhysObj(PX\ObjBody)
		Return PX\ObjBody
	EndIf 
End Function

Function SavePXMap(DirPack$)
	
	MakeFile("px.ini")
	INI_OpenFile2("px.ini")
	
	For PX.PhysXSystem = Each PhysXSystem
		If PX\Id>0
			EntCount=EntCount+1
		EndIf
	Next
		
	INI_WriteValue("Entity", "count",EntCount)	
	
	EntCount=0
	
	For PX.PhysXSystem = Each PhysXSystem
		If PX\Id>0
			
			EntCount=EntCount+1
			INI_WriteValue("Ent_"+EntCount, "ID",PX\Id)	
			INI_WriteValue("Ent_"+EntCount, "froz",PX\ObjFrozen)	
			
			
			INI_WriteValue("Ent_"+EntCount, "X",xEntityX(PX\ObjMesh))	
			INI_WriteValue("Ent_"+EntCount, "Y",xEntityY(PX\ObjMesh))	
			INI_WriteValue("Ent_"+EntCount, "Z",xEntityZ(PX\ObjMesh))
			
			INI_WriteValue("Ent_"+EntCount, "Pitch",xEntityPitch(PX\ObjMesh))	
			INI_WriteValue("Ent_"+EntCount, "Yaw",xEntityYaw(PX\ObjMesh))	
			INI_WriteValue("Ent_"+EntCount, "Roll",xEntityRoll(PX\ObjMesh))
			
			
			
			INI_WriteValue("Ent_"+EntCount, "Kinematic",PX\ObjIsKinematic)
			INI_WriteValue("Ent_"+EntCount, "KineType",PX\ObjKinematicType)	
			INI_WriteValue("Ent_"+EntCount, "Rotating",PX\ObjHasRotating)
			INI_WriteValue("Ent_"+EntCount, "RootSpeed",PX\ObjRoot)
			INI_WriteValue("Ent_"+EntCount, "DoorHo",PX\DoorType)
			
			INI_WriteValue("Ent_"+EntCount, "IsJoint",PX\IsJoint)
			
			
			If  PX\ObjKinematicType=2
				INI_WriteValue("Ent_"+EntCount, "IsLift",2)
		EndIf 
		EndIf 
	Next 
	INI_CloseFile%()
	xCopyFile ("px.ini",MapsPatch$+DirPack$+"px.ini")
	DeleteFile ("px.ini") 
End Function





Function LoadPXMap(DirPack$)
	INI_OpenFile2(MapsPatch$+DirPack$+"px.ini")
	
	EntCount = INI_ReadValue("Entity", "count", "0")
	For lft=1 To EntCount
		EntSpaw=EntSpaw+1
		
		ID=INI_ReadValue("Ent_"+EntSpaw, "ID", "0")
		froz=INI_ReadValue("Ent_"+EntSpaw, "froz", "0")
		
		xt#=INI_ReadValue("Ent_"+EntSpaw, "X", "0")
		yt#=INI_ReadValue("Ent_"+EntSpaw, "Y", "0")
		zt#=INI_ReadValue("Ent_"+EntSpaw, "Z", "0")
		
		pt#=INI_ReadValue("Ent_"+EntSpaw, "Pitch", "0")
		yat#=INI_ReadValue("Ent_"+EntSpaw, "Yaw", "0")
		rt#=INI_ReadValue("Ent_"+EntSpaw, "Roll", "0")
		
		IsJoint=INI_ReadValue("Ent_"+EntSpaw, "IsJoint", "0")
		
		
		NewEnt=SpawnBodyFunc(ID,xt#,yt#,zt#,yat#,pt#,rt#,froz)
		
		
		If IsJoint=1  
			joint1=pxJointCreateSpherical(0,GetBodyFromMesh(NewEnt),xEntityX(NewEnt,1),xEntityY(NewEnt,1),xEntityZ(NewEnt,1),  0, 1,0)
			SetJointBody(NewEnt)
			
		EndIf 
		
		
		
		
		
		DoorType=INI_ReadValue("Ent_"+EntSpaw, "DoorHo", "0")
		If DoorType=1 
			j= pxJointCreateHinge(0,GetBodyFromMesh(NewEnt), xEntityX(NewEnt,1),xEntityY(NewEnt,1),xEntityZ(NewEnt,1),0, 5, 0)
			pxJointHingeSetLimit(j, -90,90)
			DoorType(NewEnt,1)
		EndIf 
		
		iSkINEoBJ=INI_ReadValue("Ent_"+EntSpaw, "Kinematic", "0")
		If iSkINEoBJ=1 Then
			xEntityPickMode NewEnt,0
			RotSpeed=INI_ReadValue("Ent_"+EntSpaw, "RootSpeed", "0")
			SetObjKinematic(GetBodyFromMesh(NewEnt),NewEnt,1,RotSpeed)
			DebugLog "ROT"
		EndIf 
		
		
		
		
		lif=INI_ReadValue("Ent_"+EntSpaw, "IsLift", "0")
		If lif>0 Then  
			xEntityPickMode NewEnt,0
			SetObjKinematic(GetBodyFromMesh(NewEnt),NewEnt,2,10)
			lift=1
			DebugLog "lift"
		EndIf 
		
		TempLit=GetLightInRad(NewEnt)
		If TempLit<>0 And   EditorEngine=0
			DebugLog "I got light in radius"
			xEntityParent TempLit,NewEnt
			
			
			;SrtLightParrent(GetLightFromEnt(TempLit),NewEnt)
		EndIf 
		
		
		
	Next
	
	INI_CloseFile%()
End Function


Function SpawnBodyFunc(Id,x#,y#,z#,ya#,p#,r#,froz)
	For pl.PhysLib = Each PhysLib
		If Id=pl\Id
			ObjBody=SetMeshAsPhysXObj2(pl\mesh,pl\body,pl\mass,pl\material,1,pl\name$,pl\Id,froz)
			xPositionEntity ObjBody,x#,y#,z#
			xRotateEntity(ObjBody,p#,ya#,r# )
			Exit
		EndIf
	Next
	
	For PX.PhysXSystem = Each PhysXSystem
		xEntitySetBody(PX\ObjMesh,PX\ObjBody)
	Next
	Return ObjBody
End Function


Function SetMeshAsPhysXObj2(MeshForPx,BodyForPx,Mass#=10,ObjMaterial=1,KepCretae=1,ObjName$="",ID,Static)
	If MeshForPx<>0
		PX.PhysXSystem = New PhysXSystem
		PX\ObjMesh=xCopyEntity(MeshForPx)
		PX\Id=ID
		xNameEntity PX\ObjMesh,"2"
		xEntityPickMode PX\ObjMesh,1
		xPositionEntity PX\ObjMesh,0,0,0
		xRotateEntity PX\ObjMesh,0,0,0
		;lig =DCreateLight(0,0,0,90,255,255,255,2,0)
		;xTurnEntity lig,90,0,0
		;xEntityParent(lig,PX\ObjMesh,1)
		
		SetDeferredMesh(PX\ObjMesh,1)
		
		PX\ObjFrozen=Static
		PX\ObjStatic=Static
		
		PX\ObjBody=0
		If Static=0
			PX\ObjBody=pxCopyBody(BodyForPx) 
		Else
				PX\ObjBody=BodyCreateMesh(PX\ObjMesh,0)
				
				PX\ObjKep=AddWaterBodyToPhysObj(PX\ObjBody,2,10)
		EndIf 
		
		
		pxBodySetFlagRayCast (PX\ObjBody,1)
		pxBodySetFlagTriggertable(PX\ObjBody, 1)
		;If Static=1
		;pxBodySetFrozen PX\ObjBody,Static
	;EndIf 
		
		PX\ObjMaterial=ObjMaterial
		PX\ObjName$=ObjName$
		PX\ObjMass=Mass#
		
		
		
	EndIf 
	Return PX\ObjMesh
End Function



Function SetMeshAsPhysXObj(MeshForPx,BodyForPx,Mass#=10,ObjMaterial=1,KepCretae=1,ObjName$="",ID)
	If MeshForPx<>0
		PX.PhysXSystem = New PhysXSystem
		PX\ObjMesh=xCopyEntity(MeshForPx)
		PX\Id=ID
		xNameEntity PX\ObjMesh,"2"
		xEntityPickMode PX\ObjMesh,1
		xPositionEntity PX\ObjMesh,0,0,0
		;lig =DCreateLight(0,0,0,90,255,255,255,2,0)
		;xTurnEntity lig,90,0,0
		;xEntityParent(lig,PX\ObjMesh,1)
		SetDeferredMesh(PX\ObjMesh,1)
		PX\ObjBody=pxCopyBody(BodyForPx) 
		pxBodySetFlagRayCast (PX\ObjBody,1)
		pxBodySetFlagTriggertable(PX\ObjBody, 1)
		
		PX\ObjMaterial=ObjMaterial
		PX\ObjName$=ObjName$
		PX\ObjMass=Mass#
		PX\ObjKep=AddWaterBodyToPhysObj(PX\ObjBody)
		
	EndIf 
	Return PX\ObjBody
End Function

Function AddWaterBodyToPhysObj(body,damp_rot#=10,damp_pos#=10)
	
	kep_body=pxCreateKep(Rand(20,80),0,3)
	pxKepAddToBody(kep_body,body)	
	pxKepSetLocalPosition(kep_body,0,0,0)
	pxKepSetLinearDamping(kep_body,damp_pos)
	pxKepSetAngularDamping(kep_body,damp_rot)
	Return kep_body
	
End Function

Function BreakBodyInRad(rad,ent)
	For PX.PhysXSystem = Each PhysXSystem
		
		If xEntityDistance (ent,PX\ObjMesh)<rad
			
			pxBodySetFrozen(PX\ObjBody,0)
				
			
			
			
			
			
			
			DestructMesh=GetBodyDest(PX\ObjName$)
			If DestructMesh<>0
				For i=0 To xCountChildren(DestructMesh)  
					child=xGetChild(DestructMesh,i)  
					If child<>0  
						db.DestructBody = New DestructBody
						db\mesh=xCopyEntity(child)
						xShowEntity db\mesh
						SetDeferredMesh(db\mesh)
						
						
						xPositionEntity(db\mesh,xEntityX(PX\ObjMesh,1),xEntityY(PX\ObjMesh,1),xEntityZ(PX\ObjMesh,1) )	
						xRotateEntity(db\mesh,xEntityPitch(PX\ObjMesh,1),xEntityYaw(PX\ObjMesh,1),xEntityRoll(PX\ObjMesh,1) ,0)
						
						db\body=BodyCreateHull(db\mesh,1) 
						pxBodySetPosition(db\body,xEntityX(PX\ObjMesh,1),xEntityY(PX\ObjMesh,1),xEntityZ(PX\ObjMesh,1) )		
						pxBodySetRotation(db\body,xEntityPitch(PX\ObjMesh,1),xEntityYaw(PX\ObjMesh,1),xEntityRoll(PX\ObjMesh,1) )
						
						roty#=pxBodyGetRotationYaw(PX\ObjBody)
						forcex#=pxBodyGetLocalLinearSpeedX#(PX\ObjBody)
						forcey#=pxBodyGetLocalLinearSpeedY#(PX\ObjBody)
						forcez#=pxBodyGetLocalLinearSpeedZ#(PX\ObjBody)
						pxBodySetLocalLinearSpeed(db\body,forcex#,forcey#,forcez#)
						
					EndIf  
				Next  
				
				If DestructMesh<>0
					If ForHandBody<>0
						ForHandBody=0
					EndIf 
					pxDeleteBody PX\ObjBody
					DeactiveDeferredMesh(PX\ObjMesh) 
					Delete PX
					Exit 
				EndIf
			EndIf 
		EndIf 

	Next 
End Function



Function BreakBody(ent)
	For PX.PhysXSystem = Each PhysXSystem
		
		If ent=PX\ObjBody
			
			pxBodySetFrozen(PX\ObjBody,0)
			
			
			
			DestructMesh=GetBodyDest(PX\ObjName$)
			If DestructMesh<>0
				For i=0 To xCountChildren(DestructMesh)  
					child=xGetChild(DestructMesh,i)  
					If child<>0  
						db.DestructBody = New DestructBody
						db\mesh=xCopyEntity(child)
						xShowEntity db\mesh
						SetDeferredMesh(db\mesh)
						
						
						xPositionEntity(db\mesh,xEntityX(PX\ObjMesh,1),xEntityY(PX\ObjMesh,1),xEntityZ(PX\ObjMesh,1) )	
						xRotateEntity(db\mesh,xEntityPitch(PX\ObjMesh,1),xEntityYaw(PX\ObjMesh,1),xEntityRoll(PX\ObjMesh,1) ,0)
						
						db\body=BodyCreateHull(db\mesh,1) 
						pxBodySetPosition(db\body,xEntityX(PX\ObjMesh,1),xEntityY(PX\ObjMesh,1),xEntityZ(PX\ObjMesh,1) )		
						pxBodySetRotation(db\body,xEntityPitch(PX\ObjMesh,1),xEntityYaw(PX\ObjMesh,1),xEntityRoll(PX\ObjMesh,1) )
						
						roty#=pxBodyGetRotationYaw(PX\ObjBody)
						forcex#=pxBodyGetLocalLinearSpeedX#(PX\ObjBody)
						forcey#=pxBodyGetLocalLinearSpeedY#(PX\ObjBody)
						forcez#=pxBodyGetLocalLinearSpeedZ#(PX\ObjBody)
						pxBodySetLocalLinearSpeed(db\body,forcex#,forcey#,forcez#)
						
					EndIf  
				Next  
				
				If DestructMesh<>0
					If ForHandBody<>0
						ForHandBody=0
					EndIf 
					pxDeleteBody PX\ObjBody
					DeactiveDeferredMesh(PX\ObjMesh) 
					Delete PX
					Exit 
				EndIf
			EndIf 
		EndIf 
		
	Next 
End Function

Global ObjLiftState
Function UpdatePhysXObj()
	For PX.PhysXSystem = Each PhysXSystem
		
		If PX\ObjMesh<>0 And PX\ObjBody<>0
			
				If PX\ObjStatic=0
			If  PX\ObjSoundTimer>0
				PX\ObjSoundTimer= PX\ObjSoundTimer-1
			EndIf 
			
			PxBodyNForce=pxContactGetForceN#(PX\ObjBody, 0)
			
			If PxBodyNForce>ObjContactSoundForce
				If PX\ObjSoundTimer=0
					If PX\ObjSoundLastTime=0
						PX\ObjSoundLastTime=1
						PX\ObjSoundTimer=15
						;EmittSoundMat(PX\ObjMesh,PX\ObjMaterial)
						MetalRandomSound(1,4,PX\ObjMesh)
					EndIf 
				EndIf
			Else 
				PX\ObjSoundLastTime=0
			EndIf 
			
			
			
			
			
			If (PxBodyNForce>150000)  And PX\IsTrimesh=0
				pxBodySetFrozen(PX\ObjBody,0)
		EndIf 
		
		
		
			
		If PX\ObjFrozen=1 And PX\IsTrimesh=0
			PX\IsTrimesh=1
			tempcube=pxBodyCreateCube(0,0,0,0)
			xEntitySetBody(PX\ObjMesh,tempcube)
			
			
			xBodySetEntity(PX\ObjMesh,PX\ObjBody)
			xPositionEntity PX\ObjMesh,0,0,0
			
			pxDeleteBody  PX\ObjBody
			
			PX\ObjBody=BodyCreateMesh(PX\ObjMesh,0)
			
			xBodySetEntity(PX\ObjMesh,tempcube)
			
			pxDeleteBody tempcube
			
			xEntitySetBody(PX\ObjMesh,PX\ObjBody)
		EndIf 
		
		If PX\ObjFrozen=0 And PX\IsTrimesh=1 And PX\ObjKinematicType=0
			PX\IsTrimesh=0
			
			tempcube=pxBodyCreateCube(0,0,0,0)
			xEntitySetBody(PX\ObjMesh,tempcube)
			
			
			xBodySetEntity(PX\ObjMesh,PX\ObjBody)
			xPositionEntity PX\ObjMesh,0,0,0
			
			pxDeleteBody  PX\ObjBody
			
			PX\ObjBody=BodyCreateHull(PX\ObjMesh,PX\ObjMass)
			
			xBodySetEntity(PX\ObjMesh,tempcube)
			
			pxDeleteBody tempcube
			
			xEntitySetBody(PX\ObjMesh,PX\ObjBody)
			
		EndIf 
		
		
			
		If (PxBodyNForce>150000) 
			    DestructMesh=GetBodyDest(PX\ObjName$)
				If DestructMesh<>0
					For i=0 To xCountChildren(DestructMesh)  
						child=xGetChild(DestructMesh,i)  
						If child<>0  
							db.DestructBody = New DestructBody
							db\mesh=xCopyEntity(child)
							xShowEntity db\mesh
							SetDeferredMesh(db\mesh)
							
							
							xPositionEntity(db\mesh,xEntityX(PX\ObjMesh,1),xEntityY(PX\ObjMesh,1),xEntityZ(PX\ObjMesh,1) )	
							xRotateEntity(db\mesh,xEntityPitch(PX\ObjMesh,1),xEntityYaw(PX\ObjMesh,1),xEntityRoll(PX\ObjMesh,1) ,0)
							
							db\body=BodyCreateHull(db\mesh,1) 
							pxBodySetPosition(db\body,xEntityX(PX\ObjMesh,1),xEntityY(PX\ObjMesh,1),xEntityZ(PX\ObjMesh,1) )		
							pxBodySetRotation(db\body,xEntityPitch(PX\ObjMesh,1),xEntityYaw(PX\ObjMesh,1),xEntityRoll(PX\ObjMesh,1) )
							
							roty#=pxBodyGetRotationYaw(PX\ObjBody)
							forcex#=pxBodyGetLocalLinearSpeedX#(PX\ObjBody)
							forcey#=pxBodyGetLocalLinearSpeedY#(PX\ObjBody)
							forcez#=pxBodyGetLocalLinearSpeedZ#(PX\ObjBody)
							;pxBodySetLocalLinearSpeed(db\body,forcex#,forcey#,forcez#)
							
						EndIf  
					Next  
					
					If DestructMesh<>0
						If ForHandBody<>0
							ForHandBody=0
						EndIf 
						pxDeleteBody PX\ObjBody
						DeactiveDeferredMesh(PX\ObjMesh) 
						Delete PX
						Exit 
					EndIf
				EndIf 
			EndIf 
			
		EndIf
			
			If PX\ObjIsKinematic=1
				Select 	PX\ObjKinematicType
					Case 1
				;yaw = pxBodyGetRotationYaw(PX\ObjBody)+ObjRotatingSpeed
						Pitch=pxBodyGetRotationPitch(PX\ObjBody)+1
						Yaw=pxBodyGetRotationYaw(PX\ObjBody)
						roll=pxBodyGetRotationRoll(PX\ObjBody)
						PX\ObjRoot=PX\ObjRoot+PX\ObjRotatingSpeed
						
						
						If PX\ObjRoot>360 PX\ObjRoot=1
							xTurnEntity PX\Objmesh,PX\ObjRotatingSpeed,0,0
							xEntitySetBody(PX\Objmesh,PX\ObjBody)
							;pxBodySetRotation(PX\ObjBody,PX\ObjRoot,Yaw,roll)
						Case 2
							;pxBodySetFrozenRot(PX\ObjBody,1)
							;xRotateEntity(mesh,pxBodyGetRotationPitch(body),pxBodyGetRotationYaw(body),pxBodyGetRotationRoll(body),1)
							;xPositionEntity(mesh,pxBodyGetPositionX(body),pxBodyGetPositionY(body),pxBodyGetPositionZ(body),1)
							If xEntityDistance(PX\ObjMesh,Camera)<10
								HandspointerAlpha#=1
								If xkht18=1  
									PX\ObjLiftState=1-PX\ObjLiftState
								EndIf 
							EndIf
							
							If PX\ObjLiftState=1 And (PX\ObjLiftY#+84)>pxBodyGetPositionY(PX\ObjBody)
								pxBodySetPosition(PX\ObjBody,pxBodyGetPositionX(PX\ObjBody),pxBodyGetPositionY#(PX\ObjBody)+0.3,pxBodyGetPositionZ(PX\ObjBody))
								pxBodySetLocalLinearSpeed(Player_pxBody,0,-20,0)
							;	pxKinematicMove PX\ObjBody,0,0.1,0
							EndIf 
							
							If PX\ObjLiftState=0 And PX\ObjLiftY#<pxBodyGetPositionY(PX\ObjBody)
								pxBodySetLocalLinearSpeed(Player_pxBody,0,20,0)
								;pxKinematicMove PX\ObjBody,0,-0.1,0
								pxBodySetPosition(PX\ObjBody,pxBodyGetPositionX(PX\ObjBody),pxBodyGetPositionY#(PX\ObjBody)-0.3,pxBodyGetPositionZ(PX\ObjBody))
							EndIf 
							
						
						
							
							
					End Select 
					
			EndIf 
			
			If PX\ObjMesh<>0
				If PX\ObjBody<>0
					xBodySetEntity(PX\ObjMesh,PX\ObjBody)
				EndIf 
			EndIf 
			
	EndIf
	Next 
	
	UpdateDest()
End Function









Function ReadDistructDir(dir$)
	myDir=xReadDir(dir$) 
	Repeat 
		file$=xNextFile$(myDir) 
		If file$="" Then Exit 
		If xFileType(folder$+"\"+file$) = 0 Then 
			If Instr(file$,".3DS")
				df.DestructFile = New DestructFile
				df\mesh = xLoadMeshWithChild(dir$+"\"+file$)
				df\name$=Replace(Lower(file$),".3ds","")
				xHideEntity(	df\mesh)
				DebugLog df\mesh+""+df\name$
			EndIf  
		End If 
	Forever 
	xCloseDir myDir 
End Function


Function GetBodyDest(name$)
	For df.DestructFile = Each DestructFile
		If Instr(Lower(name$),Lower(df\name$))
			Return df\mesh
			Exit 
		EndIf 
    Next
End Function


Type DestructFile
	;Main
	Field name$
	Field Mesh
End Type

Type DestructBody
	;Main
	Field Body
	Field Mesh
	Field live
End Type

Function UpdateDest()
	For db.DestructBody = Each DestructBody
		xBodySetEntity(db\mesh,db\body)
	Next
End Function


Type HorisontalDoor
	Field Mesh
	Field Body
	Field x
	Field y
	Field z
	Field State
	Field Dir
	Field WantState#
	Field Frame#
	Field orient
	
	Field DPivot
	
	Field lastS
	Field Sound
End Type

Function CreatenDoor(orient)
	hd.HorisontalDoor=New HorisontalDoor
	hd\Mesh=xLoadMesh(PlayerModelPatch$+"door.3DS")
	SetDeferredMesh(hd\Mesh,1)
	hd\DPivot=xCreatePivot()
	hd\x=xEntityX(hd\Mesh)
	hd\y=xEntityY(hd\Mesh)
	hd\z=xEntityZ(hd\Mesh)+orient
	hd\orient=orient
	
	If hd\orient=1
		xTurnEntity hd\Mesh,0,180,0
	EndIf
	
	hd\Frame#=20
	xPositionEntity hd\DPivot,xEntityX(hd\Mesh),xEntityY(hd\Mesh),xEntityZ(hd\Mesh),1
	hd\State=0
End Function

Function UpdatenDoor()
	For hd.HorisontalDoor=Each HorisontalDoor
		
		
		
		
		
		If Keyp hd\State=1-hd\State
			
			
			If hd\State=1
				If hd\WantState#<hd\Frame#
					hd\WantState#=hd\WantState#+0.3
				EndIf 
			Else
				If hd\WantState#>0
					hd\WantState#=hd\WantState#-0.3
				EndIf
			EndIf 
			
		;,DoorPos,DoorSatate
			If hd\orient=0
				xPositionEntity hd\Mesh,hd\x+hd\WantState#,hd\y,hd\z,1
			Else 
				xPositionEntity hd\Mesh,hd\x-hd\WantState#,hd\y,hd\z,1
			EndIf
			
			
			
			
		Next
End Function

Function BodyCreateHull(mesh,mass#) 
	If mesh <> 0 
		Local VB = xGetMeshVB(mesh) 
		Local VB_size = xGetMeshVBSize(mesh) 
		If VB <> 0 And VB_size <>0
			Local cube_body = pxBodyCreateHull(VB, VB_size, mass#) 
		End If 
		Return cube_body 
	End If 
End Function 

Function BodyCreateMesh(mesh%,mass#)
	If mesh <> 0 
		Local VBs = xGetMeshVB(mesh) 
		Local VB_sizes = xGetMeshVBSize(mesh) 
		Local IBs = xGetMeshIB(mesh ) 
		Local IB_sizes=xGetMeshIBSize(mesh) 
		Local Trimesh=pxCreateTriMesh(VBs, IBs, VB_sizes, IB_sizes, mass#) 
		Return Trimesh 
	End If 
End Function 


Function xBodySetBody(mesh,body)
	pxBodySetPosition body ,pxBodyGetRotationPitch(body),pxBodyGetRotationYaw(body),pxBodyGetRotationRoll(body)
	pxBodySetRotation (body, pxBodyGetPositionX(body),pxBodyGetPositionY(body),pxBodyGetPositionZ(body))
End Function


Function xEntitySetBody(mesh,body)
	pxBodySetPosition body ,xEntityX(mesh,1),xEntityY(mesh,1),xEntityZ(mesh,1)
	pxBodySetRotation (body, xEntityPitch (mesh,1), xEntityYaw (mesh,1), xEntityRoll (mesh,1))
End Function

Function xBodySetEntity(mesh,body)
	xRotateEntity(mesh,pxBodyGetRotationPitch(body),pxBodyGetRotationYaw(body),pxBodyGetRotationRoll(body),1)
	xPositionEntity(mesh,pxBodyGetPositionX(body),pxBodyGetPositionY(body),pxBodyGetPositionZ(body),1)
End Function

Function xEntitySetEntity(mesh2,mesh)
	xRotateEntity mesh2 ,xEntityX(mesh,1),xEntityY(mesh,1),xEntityZ(mesh,1)
	xPositionEntity (mesh2, xEntityPitch (mesh,1), xEntityYaw (mesh,1), xEntityRoll (mesh,1))
End Function







;~IDEal Editor Parameters:
;~F#9B#A6#BA#114
;~C#Blitz3D