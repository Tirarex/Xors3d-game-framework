
Global  MenuButtonSound 
Dim StepSound(4,4)
Dim impact_metal(6)
Global oldNUM


Function LoadSounds()	
	
	MenuButtonSound =xLoad3DSound(SoundPatch$+"MenuButton.wav") 
	
	
	For i=1 To 4
		StepSound(1,i)=xLoad3DSound(SoundPatch$+"footsteps\metal"+Str(i)+".wav")
	Next
	For i=1 To 4
		StepSound(2,i)=xLoad3DSound(SoundPatch$+"footsteps\earth"+Str(i)+".wav")
	Next
	For i=1 To 4
		StepSound(3,i)=xLoad3DSound(SoundPatch$+"footsteps\wood"+Str(i)+".wav")
	Next
	For i=1 To 4
		StepSound(4,i)=xLoad3DSound(SoundPatch$+"footsteps\concrete"+Str(i)+".wav")
	Next
	
	For i=1 To 4
		impact_metal(i)=xLoad3DSound(SoundPatch$+"phys\Impact_"+Str(i)+".wav")
	Next
End Function

Function MetalRandomSound(min,max,ent)
	num = Rand(min,max)
	xEmitSound( impact_metal(num),ent)
End Function 



Function CalcPlayerStep(ent,surf)
	;num = Rand(3)+1
	For i=0 To 100
		num = Rand(0,3)+1
		If num<>oldNUM
			oldNUM=num
			Exit 
		EndIf 
	Next 
	xEmitSound(StepSound(surf,num),camera)
End Function 


Type SoundSystem 
	Field SoundEmiter
	Field Volume#
	Field Sound
	Field TriggerID
	Field SoundType
End Type


Function LoadAudioSystem(SoundInIPatch$)
	DebugLog "Sound:" +SoundInIPatch$
	
	INI_OpenFile2(SoundInIPatch$)
	SoundsCount = INI_ReadValue("Sounds", "SoundsCount", "0") 
	For SC=1 To SoundsCount
		SPoint=SPoint+1
		
		
		SoundFile$=INI_ReadValue("Sound_"+SPoint, "SoundFile", "0")
		Volume#=   INI_ReadValue("Sound_"+SPoint, "SoundVolume", "0")
		TriggerID= INI_ReadValue("Sound_"+SPoint, "SoundTrigger", "0")
		SoundLoop= INI_ReadValue("Sound_"+SPoint, "SoundLoop", "0")
		SoundType= INI_ReadValue("Sound_"+SPoint, "SoundType", "0")
		SoundEntID=INI_ReadValue("Sound_"+SPoint, "SoundEntID", "0")
		SoundAplay=INI_ReadValue("Sound_"+SPoint, "SoundAplay", "0")
		
		SoundX=    INI_ReadValue("Sound_"+SPoint, "SoundX", "0")
		SoundY=    INI_ReadValue("Sound_"+SPoint, "SoundY", "0")
		SoundZ=    INI_ReadValue("Sound_"+SPoint, "SoundZ", "0")
		
		AddSoundToMap(EmitterEnt,EmitterEntID,SoundFile$,TriggerID,Volume#,SoundLoop,SoundType,SoundAplay,SoundX,SoundY,SoundZ)
		
	Next	
	INI_CloseFile%()
	
End Function


Function AddSoundToMap(EmitterEnt,EmitterEntID,SoundFile$,TriggerID,Volume#,SoundLoop,SoundType,SoundAplay,SoundX,SoundY,SoundZ)
	ss.SoundSystem = New SoundSystem
	Select SoundType
		Case 1
			ss\Sound=xLoad3DSound(SoundPatch$+SoundFile$)
		Case 2
			ss\Sound=xLoadSound(SoundPatch$+SoundFile$)
		Default
			DebugLog "NoSound"
	End Select 
	
	xSoundVolume ss\Sound,Volume#
	
	If SoundLoop=1
		xLoopSound ss\Sound
	EndIf 
	
	If EmitterEnt<>0
		ss\SoundEmiter=EmitterEnt
	Else
		ss\SoundEmiter=xCreatePivot()
		xPositionEntity ss\SoundEmiter,SoundX,SoundY,SoundZ
	EndIf 
	
	If SoundAplay=1
		Select SoundType
			Case 1
				xEmitSound( ss\Sound,ss\SoundEmiter)
			Case 2
				xPlaySound ss\Sound
			Default
		End Select 
	EndIf 
	ss\TriggerID=TriggerID
End Function


Function DeleteMapSounds()
	For ss.SoundSystem = Each SoundSystem
		xFreeEntity ss\SoundEmiter
		xFreeSound ss\Sound
		Delete ss
	Next 
End Function

Global PauseLiz=0

Function UpdateSoundSystem()
	For ss.SoundSystem = Each SoundSystem
		If PauseLiz=1
			xPauseChannel(ss\Sound)
		Else 
			xResumeChannel(ss\Sound)
		EndIf 
	Next 
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D