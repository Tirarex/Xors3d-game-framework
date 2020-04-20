Global NowChapter=0,syjet=1


;Chapter 1
Global camera_b,Ch1_Stage=0,StopTimer=2500

Function Chapter_1_Scripts(Dir$)
	EventMesh=xLoadAnimMesh(Dir$)
	xEntityPickMode EventMesh,2
	xHideEntity EventMesh
	Ch1_Stage=0
	DebugLog "loaded" + EventMesh
	camera_b=xFindChild(EventMesh,"camera")
	xShowEntity camera_b
	AddDeferredReciver(camera_b,1)
End Function

Function UpdateChapter_1()
	
	
	Select Ch1_Stage
		Case 0
			
			
			pxBodySetPosition Player_pxBody,300,5,1492
			StopPlayer()
			;fly=0
			;Ch1_Stage=3
			
			
			
			xMoveEntity camera_b,0,0.5,0
			
			xPositionEntity camera,xEntityX(camera_b),xEntityY(camera_b),xEntityZ(camera_b)
			If xEntityz(camera_b)>110
				Ch1_Stage=1
			EndIf
			fly=1
			UpdateFlyCam(0)
			
	Case 1 
		StopTimer=StopTimer-10
		If StopTimer<0
			Ch1_Stage=2
		EndIf 
		fly=1
		UpdateFlyCam(0)
	Case 2
		xMoveEntity camera_b,0,0.5,0
		
		xPositionEntity camera,xEntityX(camera_b),xEntityY(camera_b),xEntityZ(camera_b)
		fly=1
		UpdateFlyCam(0)
		
		If xEntityz(camera_b)>1000
			;UnLoadGame()
			;LoadGame("DevTest")
			pxBodySetPosition Player_pxBody,300,5,1492
			StopPlayer()
			fly=0
			Ch1_Stage=3
		EndIf
		
		
		
	Case 3
		;fly=0
			
End Select 

DebugLog Ch1_Stage

End Function


Function UpdateScripts()
	Select NowChapter
		Case 1
			UpdateChapter_1()
			
		Case 2
	End Select
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D