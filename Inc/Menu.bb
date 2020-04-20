;Menu
Global mouseht1
Global exityes,gamestate,ShowMenu,ShowMaps,ShowMode,SettingsMenu
Global GfxModeCount,GfxSelected=1
Global kn0,kn1,kn2,blinker
Global map_count=0
Global map_id=0
Global map_name$="NOMAP"
Global MenuBlur=0


Function GiveState$(stateI)
	If stateI=1
		Return "Enabled"
	Else 
		Return "Disabled"
	EndIf 
	
End Function


Global ShadowMode


Function GetShadowMode$(smode)
	GlobalShadows=1
	Select smode
		Case 0
			GlobalShadows=0
		Case 1
			ShadowsSize=128
		Case 2 
			ShadowsSize=256
		Case 3
			ShadowsSize=512
		Case 4
			ShadowsSize=1024
		Case 5
			ShadowsSize=2048
		Case 6
			ShadowsSize=4096
	End Select
	
	If smode<>0
		Return "Enabled,size-"+ShadowsSize
	Else
		Return "Disabled"
	EndIf 
	
End Function



Function SetupShadows(smode)
	GlobalShadows=1
	Select smode
		Case 0
			GlobalShadows=0
		Case 1
			ShadowsSize=128
		Case 2 
			ShadowsSize=256
		Case 3
			ShadowsSize=512
		Case 4
			ShadowsSize=1024
		Case 5
			ShadowsSize=2048
		Case 6
			ShadowsSize=4096
	End Select
	
End Function

Function DrawSwttings()
	GfxModeCount=CountGfxModes3D()
	
	If ReDrawButton(Getletter$(7)+GfxModeWidth(GfxSelected)+"x"+GfxModeHeight(GfxSelected),(w/2)-160,(h/2)-00) 
		GfxSelected=GfxSelected+1
		If GfxSelected= GfxModeCount+1
			GfxSelected=1
		EndIf 
		
	EndIf 
	
	If ReDrawButton(Getletter$(8)+GiveState$(fullS),(w/2)-160,(h/2)+20)
		fullS=1-fullS
	EndIf 
	
	If ReDrawButton(Getletter$(9)+GiveState$(vsunc),(w/2)-160,(h/2)+40) 
		vsunc=1-vsunc
	EndIf 
	
	If ReDrawButton(Getletter$(10)+GetShadowMode$(ShadowMode),(w/2)-160,(h/2)+60) 
		ShadowMode=ShadowMode+1
		If ShadowMode=7 ShadowMode=0
	EndIf 
	
	If ReDrawButton(Getletter$(11)+GiveState$(PostSSLR),(w/2)-160,(h/2)+80)
		PostSSLR=1-PostSSLR
	EndIf 
	
	If ReDrawButton(Getletter$(12)+GiveState$(PostFXAA),(w/2)-160,(h/2)+100) 
		PostFXAA=1-PostFXAA
	EndIf 
	
	If ReDrawButton(Getletter$(13)+GiveState$(DeferredParalax),(w/2)-160,(h/2)+120) 
		DeferredParalax=1-DeferredParalax
	EndIf 
	
	If ReDrawButton(Getletter$(14)+GiveState$(EnableParticleLighting),(w/2)-160,(h/2)+140) 
		EnableParticleLighting=1-EnableParticleLighting
	EndIf 
	
	If ReDrawButton(Getletter$(15)+GiveState$(PostProcessing),(w/2)-160,(h/2)+160) 
		PostProcessing=1-PostProcessing
	EndIf 
	
	
	
	If ReDrawButton(Getletter$(18),(w/2)-120,(h/2)+200)
		oldW=w
		oldH=h
		w=GfxModeWidth(GfxSelected)
		h=GfxModeHeight(GfxSelected)
		SaveSettings()
		w=oldW
		h=oldH
		
	EndIf 
End Function



Function updatemenu()
	xSetColor 255,255,255
	xSetImageFont MenuFont
	Select gamestate
		Case 0 ;Main menu
			MenuBlur=0
			If ReDrawButton(Getletter$(1),50,h-130) Then ShowMode=1-ShowMode  SettingsMenu=0
			
			
			If ShowMode=1 And SettingsMenu=0
				ReDrawButton Getletter$(5),190,h-160 
				
				
				If ReDrawButton (Getletter$(6),190,h-130 ) ShowMaps=1-ShowMaps
					
					If ShowMaps=1
						MenuBlur=1
						
						If ReDrawButton ("NewMap",330,h-220)   And gamestate=0
							LoadGame("NewMap")
							gamestate=2
							ShowMode=0
						EndIf
						
						
						If ReDrawButton ("Chapter 1",330,h-190)  And gamestate=0
							LoadGame("Chapter_2")
							gamestate=2
							ShowMode=0
						EndIf
						
						
						
						If ReDrawButton ("hltest",330,h-100  )	 And gamestate=0
							LoadGame("hltest") 
							gamestate=2
							ShowMode=0
						EndIf
						
						
						If ReDrawButton ("map",330,h-70  )	 And gamestate=0
							LoadGame("map") 
							gamestate=2
							ShowMode=0
						EndIf
						If ReDrawButton ("svalker",330,h-40  )	 And gamestate=0
							LoadGame("svalker") 
							gamestate=2
							ShowMode=0
						EndIf
				EndIf
			EndIf
			
			xDrawImageEx Gamelogo,w/2,h/3
			
			
			If ReDrawButton(Getletter$(2),50,h-100) Then SettingsMenu=1-SettingsMenu ShowMode=0
			
			
			
			If SettingsMenu=1
				DrawSwttings()
			EndIf 
			
			
			
			
			
			ReDrawButton(Getletter$(3),50,h-70)
			
			
			If ReDrawButton(Getletter$(4),50,h-40) Then exityes=1 ShowMenu=1-ShowMenu
			
			
			xSetRotation(0)
			
			FIDef()
			
		Case 1 ;pause
			MenuBlur=1
			If ReDrawButton(Getletter$(16),50,h-100) Then 
				gamestate=2 
				SettingsMenu=0
				CenterMoise()
			EndIf 
			
			If ReDrawButton(Getletter$(2),50,h-70) Then 
				SettingsMenu=1-SettingsMenu
			EndIf 
			
			
			If SettingsMenu=1
				MenuBlur=0
				DrawSwttings()
			EndIf 
			
			If ReDrawButton(Getletter$(17),50,h-40) Then gamestate=0: UnLoadGame():SettingsMenu=0
			
			If xKeyHit(KEY_ESCAPE)
				gamestate=2
				SettingsMenu=0
				CenterMoise()
			EndIf
			
			nullkeys()
		Case 2 ;In game
			If xKeyHit(KEY_ESCAPE)
				gamestate=1
			EndIf
			MenuBlur=0
		Case 3 ;Settings
			
			
	End Select
	
End Function

Function CenterMoise()
	xMoveMouse (xGraphicsWidth()/2,xGraphicsHeight()/2)
	Updateipunt()
End Function



Function ReDrawButton(bText$,x,y,c=0)
	If c=0
		xSetColor (255,255,255)
	EndIf 
	
	mx=xMouseX() 
	my=xMouseY() 
	
	BFuncRet=0
	
	Tw=xStringWidthEx(bText$)
	Th=xStringHeightEx(bText$)
	
	If xRectsOverlap(x,y,Tw,Th,mx,my,1,1) Then
		xSetColor (128,128,128)
		If msh1=True Then 	
			xSetColor (255,0,0)
            BFuncRet=1
			xEmitSound(MenuButtonSound,camera)
		EndIf 
	EndIf
	
	xDrawText% bText$,x,y
	
	xSetColor (255,255,255)
	
	Return BFuncRet
End Function












;~IDEal Editor Parameters:
;~C#Blitz3D