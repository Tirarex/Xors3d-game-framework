Type LightEntity
	Field Light
	Field ControlMesh
	Field Flare
	Field ID
	
	;Rotating
	Field RotatingT
	Field RotSpeedX#
	Field RotSpeedY#	
	Field RotSpeedZ#	
	
	
	;Blinking
	Field Blink
	Field BlinkNumb
	Field BlinkState
	
End Type
;Settings



Function CreateControledLight(ID=0)
	l.LightEntity = New LightEntity
	l\ID=ID
	l\ControlMesh=xCreatePivot()
	
	l\light=CreateDeferredLight(1,l\ControlMesh)
	DeferredLightColor(l\light,255, 255,255)
	DeferredLightRange(l\light,10)
	l\BlinkNumb=10
	Return l\ControlMesh
End Function
Global dgh



Function CreateControledLight2()
	l.LightEntity = New LightEntity
	l\ControlMesh=xLoadMesh("DefaultData\models\Light.fbx")
	;xScaleMesh l\ControlMesh,0.5,0.5,0.5
	xEntityPickMode l\ControlMesh,2
	AddDeferredReciver(l\ControlMesh,0)
	
	
	l\light=CreateDeferredLight(1,l\ControlMesh)
	DeferredLightColor(l\light,255, 255,255)
	DeferredLightRange(l\light,10)
	l\BlinkNumb=10
	
	
	If FlareEnabled=1
		l\Flare=xCreateCube()
		xScaleMesh l\Flare,1,1,0
		xEntityTexture l\Flare,xLoadTexture(TexturesPatch$+"Light3.png",2)
		AddAlphaEntity(l\Flare,0.5,1,1,1)
	EndIf
	
	
	xNameEntity l\ControlMesh,"1"
	Return l\ControlMesh
End Function

Function UpdateLightEnt()
	
	
	For L.LightEntity = Each LightEntity
		
		If l\Blink=1
			BlinkRnd=Rand(0,l\BlinkNumb)
			If BlinkRnd=0
				l\BlinkState=1-l\BlinkState
				DeferredLightState(l\light,l\BlinkState)
			EndIf
		Else
			DeferredLightState(l\light,1)
		EndIf 
		
		If l\RotatingT=1
			xTurnEntity l\ControlMesh ,l\RotSpeedX#,l\RotSpeedY#,l\RotSpeedZ#
		EndIf
	Next 
End Function


Function GetLightInRad(ent)
	For L.LightEntity = Each LightEntity
		If ent<>0
			If xEntityDistance(ent,l\ControlMesh)<40
			Return l\ControlMesh
		EndIf 
	EndIf
	Next 
End Function


;Comands For Control Light Blinking
Function DeferredLightBlinkState(Light,Blink=0)
	For l.LightEntity= Each LightEntity
		If l\ControlMesh=Light
			l\Blink=Blink
		EndIf 
	Next
End Function

Function DeferredLightBlinkNumber(Light,BlinkN=10)
	For l.LightEntity= Each LightEntity
		If l\ControlMesh=Light
			l\BlinkNumb=BlinkN
		EndIf 
	Next
End Function

Function GetLightBlinkState(Light)
	For l.LightEntity= Each LightEntity
		If l\ControlMesh=Light
			Return l\Blink
		EndIf 
	Next
End Function

Function GetLightBlinkNumber(Light)
	For l.LightEntity= Each LightEntity
		If l\ControlMesh=Light
			Return l\BlinkNumb
		EndIf 
	Next
End Function


Function SaveLightPatch(DirPack$)
	
	
	MakeFile("save.ini")
	INI_OpenFile2("save.ini")
	
	For L.LightEntity = Each LightEntity
		AllLiCount	=AllLiCount+1
	Next
	INI_WriteValue("Lights", "LightCount",AllLiCount)
	LightID=0
	
	For L.LightEntity = Each LightEntity
		LightID=LightID+1
		
		
		INI_WriteValue("light_"+LightID, "X",xEntityX(l\ControlMesh))
		INI_WriteValue("light_"+LightID, "Y",xEntityY(l\ControlMesh))
		INI_WriteValue("light_"+LightID, "Z",xEntityZ(l\ControlMesh))
		
		INI_WriteValue("light_"+LightID, "yaw",xEntityYaw(l\ControlMesh))
		INI_WriteValue("light_"+LightID, "pitch",xEntityPitch(l\ControlMesh))
		INI_WriteValue("light_"+LightID, "roll",xEntityRoll(l\ControlMesh))
		
		INI_WriteValue("light_"+LightID, "Red",GetLightR(GetLightFromEnt(l\ControlMesh)))
		INI_WriteValue("light_"+LightID, "Green",GetLightG(GetLightFromEnt(l\ControlMesh)))
		INI_WriteValue("light_"+LightID, "Blue",GetLightB(GetLightFromEnt(l\ControlMesh)))
		INI_WriteValue("light_"+LightID, "Blue",GetLightB(GetLightFromEnt(l\ControlMesh)))
		INI_WriteValue("light_"+LightID, "AttenMultipler",GetLightAttenMultipler#(GetLightFromEnt(l\ControlMesh)))
		
		INI_WriteValue("light_"+LightID, "Range",GetLightRange(GetLightFromEnt(l\ControlMesh)))
		INI_WriteValue("light_"+LightID, "Shadows",GetLightShadowsS(GetLightFromEnt(l\ControlMesh)))
		INI_WriteValue("light_"+LightID, "Type",GetLightType(GetLightFromEnt(l\ControlMesh)))
		
		INI_WriteValue("light_"+LightID, "Blinking",GetLightBlinkState(l\ControlMesh))
		INI_WriteValue("light_"+LightID, "BlinkingNumer",GetLightBlinkNumber(l\ControlMesh))
		INI_WriteValue("light_"+LightID, "LightID",LightID)
		DebugLog "inner:"+GetLightInner#(GetLightFromEnt(l\ControlMesh))
		INI_WriteValue("light_"+LightID, "Inner",GetLightInner#(GetLightFromEnt(l\ControlMesh)))
		INI_WriteValue("light_"+LightID, "Outer",GetLightOuter#(GetLightFromEnt(l\ControlMesh)))
		
		INI_WriteValue("light_"+LightID, "Rotate",l\RotatingT)
		INI_WriteValue("light_"+LightID, "RoX",l\RotSpeedX#)
		INI_WriteValue("light_"+LightID, "RoY",l\RotSpeedY#)
		INI_WriteValue("light_"+LightID, "RoZ",l\RotSpeedZ#)
		
		INI_WriteValue("light_"+LightID, "Scatter",GetScatter#(GetLightFromEnt(l\ControlMesh)))
		
	Next	
	
	
	
	INI_CloseFile%()
	
	xCopyFile ("save.ini",MapsPatch$+DirPack$+"save.ini")
	DeleteFile("save.ini")
End Function

Function LoadLightPatch(IsEditor=0,DirPack$)
	
		LPatch$=MapsPatch$+DirPack$+"save.ini"
		DebugLog  LPatch$
	
	
	INI_OpenFile2(LPatch$)
	LightToLoadCount = INI_ReadValue("Lights", "LightCount", "0") 
	For lf=1 To LightToLoadCount
		LiIdNxt=LiIdNxt+1
		If IsEditor=0
			CoMesh=CreateControledLight2()
			xNameEntity CoMesh,"1"
		Else 
			CoMesh=CreateControledLight()
		EndIf 
			
		xPositionEntity CoMesh,INI_ReadValue("light_"+LiIdNxt, "X", "0") ,INI_ReadValue("light_"+LiIdNxt, "Y", "0") ,INI_ReadValue("light_"+LiIdNxt, "Z", "0") 
		xRotateEntity   CoMesh,INI_ReadValue("light_"+LiIdNxt, "pitch", "0"),INI_ReadValue("light_"+LiIdNxt, "yaw","0"),INI_ReadValue("light_"+LiIdNxt, "roll", "0") 
		
		DeferredLightType(GetLightFromEnt(CoMesh),INI_ReadValue("light_"+LiIdNxt, "Type", "0"))
		DeferredLightRange(GetLightFromEnt(CoMesh),INI_ReadValue("light_"+LiIdNxt, "Range", "0"))
		DeferredLightShadows(GetLightFromEnt(CoMesh),INI_ReadValue("light_"+LiIdNxt, "Shadows", "0"))
		DeferredLightBlinkState(GetLightFromEnt(CoMesh),INI_ReadValue("light_"+LiIdNxt, "Blinking", "0"))
		
		r=INI_ReadValue("light_"+LiIdNxt, "Red", "80")
		g=INI_ReadValue("light_"+LiIdNxt, "Green", "0")
		b=INI_ReadValue("light_"+LiIdNxt, "Blue", "0")
		AttM#=INI_ReadValue("light_"+LiIdNxt, "AttenMultipler", "1")
		
		liInner#= INI_ReadValue("light_"+LiIdNxt, "Inner", "1")
		liOuter#= INI_ReadValue("light_"+LiIdNxt, "Outer", "1")
		
		DeferredLightConeAngles(GetLightFromEnt(CoMesh),liInner#,liOuter#)
		DeferredLightBlinkState(CoMesh,INI_ReadValue("light_"+LiIdNxt, "Blinking", "0"))
		DeferredLightBlinkNumber(CoMesh,INI_ReadValue("light_"+LiIdNxt, "BlinkingNumer", "10"))
		
		DeferredLightColor(GetLightFromEnt(CoMesh),r,g,b,AttM#)
		
		
		scatterpow#=INI_ReadValue("light_"+LiIdNxt, "Scatter", "0")
		SetScatter(GetLightFromEnt(CoMesh),scatterpow#)
		
		RotLit=INI_ReadValue("light_"+LiIdNxt, "Rotate", "0")
		
		If RotLit=1
		For l.LightEntity= Each LightEntity
			If CoMesh=l\ControlMesh
				l\RotatingT=1
				l\RotSpeedX#=INI_ReadValue("light_"+LiIdNxt, "RoX", "0")
				l\RotSpeedY#=INI_ReadValue("light_"+LiIdNxt, "RoY", "0")
				l\RotSpeedZ#=INI_ReadValue("light_"+LiIdNxt, "RoZ", "0")
				DebugLog "ROTAAAAAAAAATING"
			EndIf 
		Next	
	EndIf 
		
	Next	
	INI_CloseFile%()
End Function


Function CopyLight(LightEnt)
	If LightEnt<>0
		NewLi=CreateControledLight2()
		
		LIGR=GetLightR(GetLightFromEnt(LightEnt))
		LIGG=GetLightG(GetLightFromEnt(LightEnt))
		LIGB=GetLightB(GetLightFromEnt(LightEnt))
		LIGMULT#=GetLightAttenMultipler#(GetLightFromEnt(LightEnt))
		
		DeferredLightColor(GetLightFromEnt(NewLi),LIGR,LIGG,LIGB,LIGMULT#)
		DeferredLightRange(GetLightFromEnt(NewLi),GetLightRange(GetLightFromEnt(LightEnt)))
		DeferredLightType(GetLightFromEnt(NewLi),GetLightType(GetLightFromEnt(LightEnt)))
		DeferredLightShadows(GetLightFromEnt(NewLi),GetLightShadowsS(GetLightFromEnt(LightEnt)))
		
		
		DeferredLightBlinkState(GetLightFromEnt(NewLi),GetLightBlinkState(LightEnt))
		DeferredLightBlinkNumber(GetLightFromEnt(NewLi),GetLightBlinkNumber(LightEnt))
		
		xPositionEntity NewLi,xEntityX(LightEnt),xEntityY(LightEnt),xEntityZ(LightEnt)
		xRotateEntity   NewLi,xEntityPitch(LightEnt),xEntityYaw(LightEnt),xEntityRoll(LightEnt)
		
		Return NewLi
	EndIf 
	
End Function

Function GetLightFromEnt(ENT)
	For l.LightEntity= Each LightEntity
		If ENT=l\ControlMesh Or ENT=l\light
			Return 	l\light
		EndIf 
	Next		
End Function

Function GetLightFromID(ID)
	For l.LightEntity= Each LightEntity
		If ID=l\ID
			Return 	l\light
		EndIf 
	Next		
End Function

Function GetLightEntFromID(ID)
	For l.LightEntity= Each LightEntity
		If ID=l\ID
			Return 	l\ControlMesh
		EndIf 
	Next		
End Function

Function DeleteLight(Light)
	For L.LightEntity = Each LightEntity
		If Light=l\ControlMesh
			DeleteDeferredLight(l\light)
			DeleteDeferredMesh(l\ControlMesh)
			;xFreeEntity l\ControlMesh
			Delete l
		EndIf 
	Next
End Function
;~IDEal Editor Parameters:
;~F#6B#73#11E#126#12E
;~C#Blitz3D