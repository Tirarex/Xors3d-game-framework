Include "Deferred\REF.bb"


;Deferred 
Global DeferredCamera
Global DeferredMeshShader
Global DeferredLightShader
Global DeferredPoly
Global DeferredParalax=0

Global ScreenW
Global ScreenH

;DeferredTextures
Global DeferredNormals
Global DeferredAlbedo
Global DeferredDepth
Global DeferredAdvMaterials
Global DeferredResult

Global DefaultTexture
Global DefaultNormal
Global DefaultSpecular
Global DefaultBright
Global DefaultFullBright
Global OldTextureBuffer,OldTextureSize=512


;Transperent System
Global AlphaShader 
Global AlphaPivot 
Global RefractionTexture
Global RefractionTexSize
Global EnableParticleLighting=1

;Shadows
Global GlobalShadows=1
Global ShadowCamera
Global ShadowsSize=64
Global ShadowTimer=10

;Post Processing
Global PostProcessing=1
Global PostFXAA=0
Global PostSSAO=1
Global SSAO_BLUR=1
Global PostSSLR=0
Global ERRRFACTOR#=0.003

Global PostFXAA_Texture
Global PostFXAA_Shader
Global PostFXAALevels=1

Global PostSSAO_Texture
Global PostSSAO_TexBlur
Global PostSSAO_Poly
Global PostSSAO_Shader



Global FallOfExp#=0.1
Global LightPivot

Type AlphaEntity 
	Field Mesh
	Field Alpha#
	Field Soft#
	Field Modo
End Type


Type DeferredLight
	Field CullMesh
	
	Field Mesh1
	Field Mesh2
	Field LightType
	Field Parent
	
	
	Field Red#
	Field Green#
	Field Blue#
	Field LightAttenMultipler#
	Field Radius
	Field Name$
	Field State
	
	Field X#
	Field Y#
	Field Z#
	
	Field SpotSize#
	Field SpotSmoth#
	
	Field Shadows
	Field ShadowsDisableOp
	Field ShadowTexture
	
	
	
	Field scatterpow#
End Type

Type DeferredReciver
	Field Mesh 
	Field Tech 
	Field ShadowsState 
	Field Surface
End Type


Global EnvCube


Global SkinShader

;Loading Deferred Resources and Effects
Function InitializeDeferred(camera)
    ShaderPack=xMountPackFile("Deferred\Deferred.zip", "Deferred\", "DeferredPasswd");
	
	DeferredLightShader 	= xLoadFXFile("Deferred\DeferredFinal.fx")
	DeferredMeshShader      = xLoadFXFile("Deferred\DeferredMRT.fx")
	
	DefaultTexture=xLoadTexture(    "Deferred\textures\DefaultTexture.png")
	DefaultNormal=xLoadTexture(     "Deferred\textures\DefaultNormal.png")
	DefaultSpecular=xLoadTexture(   "Deferred\textures\DefaultSpecular.png")
	DefaultBright=xLoadTexture(     "Deferred\textures\DefaultBright.png")
	DefaultFullBright=xLoadTexture( "Deferred\textures\DefaultFullBright.png")
	
	ScreenW=xGraphicsWidth()
	ScreenH=xGraphicsHeight()
	DeferredCamera=camera
	
	DeferredPoly 			= xCreatePostEffectPoly(DeferredCamera, 1)
	DeferredAlbedo  		= xCreateTexture(ScreenW, ScreenH,2)
	DeferredNormals 		= xCreateTexture(ScreenW, ScreenH,4096)
	DeferredAdvMaterials  	= xCreateTexture(ScreenW, ScreenH)
	
	DeferredResult 			= xCreateTexture(ScreenW, ScreenH) 
	OldTextureBuffer		= xCreateTexture(OldTextureSize, OldTextureSize) 
	
	LightPivot=xCreatePivot()
	xSetEntityEffect 	DeferredPoly, DeferredLightShader
	xSetEffectTechnique DeferredPoly, "TEMP"
	xSetEffectTexture 	DeferredPoly, "tGBScreen",  DeferredAlbedo
	xSetEffectTexture 	DeferredPoly, "tGBNormals", DeferredNormals
	xSetEffectTexture 	DeferredPoly, "tBightles",  DeferredAdvMaterials                                                            
	
	
	ShadowCamera=xCreateCamera()
	xCameraZoom ShadowCamera,0
	xHideEntity ShadowCamera
	xCameraRange ShadowCamera,0.1,250
	
	xSetEffectVector LightPivot,"Res", ScreenW, ScreenH, 1
	
	;If PostFXAA=1
	PostFXAA_Shader = xLoadPostEffect ("Deferred\DeferredFXAA.fx")
	xSetPostEffect 1,     PostFXAA_Shader, "PostProcess"
	xSetPostEffectFloat   PostFXAA_Shader, "BUFFER_RCP_WIDTH",Float(1)/ Float(ScreenW)
	xSetPostEffectFloat   PostFXAA_Shader, "BUFFER_RCP_HEIGHT",Float(1)/ Float(ScreenH)
	PostFXAA_Texture = xCreateTexture(ScreenW, ScreenH) 
	xSetPostEffectTexture PostFXAA_Shader, "gScreenTexture", PostFXAA_Texture
    ;EndIf 
	
	;If PostSSAO=1
	PostSSAO_Texture=xCreateTexture(xGraphicsWidth()/1,xGraphicsHeight()/1)
	PostSSAO_TexBlur=xCreateTexture(xGraphicsWidth(),xGraphicsHeight())
	PostSSAO_Shader=xLoadFXFile("Deferred\DeferredSSAO.fx");
		PostSSAO_Poly=xCreatePostEffectPoly(DeferredCamera,1);
		xSetEntityEffect(PostSSAO_Poly,PostSSAO_Shader);
		xSetEffectTechnique(PostSSAO_Poly,"SSAO");
		xSetEffectTexture(PostSSAO_Poly,"Depth",DeferredAdvMaterials);
		xSetEffectTexture(PostSSAO_Poly,"Screen",PostSSAO_Texture);
		xSetEffectTexture(PostSSAO_Poly,"Diffuse",PostSSAO_TexBlur);
		xSetEffectTexture(PostSSAO_Poly,"Random",xLoadTexture("Deferred\textures\rot2.png"));
		xSetEffectTexture 	PostSSAO_Poly, "tGBNormals", DeferredNormals
	;EndIf 	
		EnvCube=xCreateTexture(256,256,1+128)
		
		LoadingAlphaSystem(512)
		xSetSkinningMethod SKIN_HARDWAREVS
		
		SkinShader = xLoadFXFile("Deferred\skinning.fx")
		
		;AnimShader
		
		LoadHDR()
		xUnmountPackFile  ( ShaderPack )  
End Function

Function ApplySkinShader(mesh)
	xSetFrustumSphere(mesh, 0,0,0, 1000)
	xSetEntityEffect mesh, SkinShader
	xSetBonesArrayName mesh, "bonesMatrixArray"
	xSetEffectTechnique mesh, "Skinned"
End Function




;Light And Light Controls
Function CreateDeferredLight(LightType=1,Parrent=0)
	
	l.DeferredLight = New DeferredLight
	
	l\Radius=100
	l\State=1
	l\Red#=1
	l\Green#=1
	l\Blue#=1
	l\LightType=LightType
	l\SpotSize#=0.6
	l\SpotSmoth#=20
	
	l\Parent=xCreatePivot(Parrent)
	
	
	;If l\LightType=5
	;	l\CullMesh = xCreatePostEffectPoly(DeferredCamera,1);
	;Else
	l\CullMesh = xCreateSphere(16)
;EndIf 

	l\Mesh1= xCreatePivot()
	
	l\Mesh2= xCreatePivot()
;	xEntityFX l\CullMesh,16
;	xEntityAlpha l\CullMesh,0
	xScaleEntity l\CullMesh,l\Radius,l\Radius,l\Radius
	
	xEntityParent l\CullMesh, LightPivot,1
	
	xSetEntityEffect 	l\CullMesh, DeferredLightShader
	xSetEffectTechnique(l\CullMesh, "DeferredPointSphere")
	
	xSetEffectTexture 	l\CullMesh, "tGBScreen" ,  DeferredAlbedo
	xSetEffectTexture 	l\CullMesh, "tGBNormals", DeferredNormals
	xSetEffectTexture 	l\CullMesh, "tBightles" ,  DeferredAdvMaterials 
	
	
	
	;xSetEffectTexture 	l\CullMesh, "LightCubemapT",  xLoadTexture("pointCube.dds",128) 
	
;	EntityPickMode l\CullMesh,2
	
	
	l\Shadows=0
	l\ShadowTexture=xCreateTexture(ShadowsSize,ShadowsSize, 1+128+256)
	
	l\X#=0
	l\Y#=0
	l\X#=0
	
	;xEntityPickMode l\mesh,2
	;RenderCubemap(l\texture,l\mesh)
	;xSetEntityEffect l\CullMesh, gDeferredShader
	;xPositionEntity l\mesh,x#,y#,z#
	
	Return l\Parent
	
End Function

Function GetLightInner#(Light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			Return l\SpotSize#
		EndIf 
	Next
End Function

Function GetLightOuter#(Light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			Return l\SpotSmoth#
		EndIf 
	Next
End Function

Function DeferredLightRange(Light,Radius=100)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			l\Radius=Radius
			xScaleEntity l\CullMesh,l\Radius,l\Radius,l\Radius
		EndIf 
	Next
End Function

Function SrtLightParrent(Light,Parrent)
	For l.DeferredLight= Each DeferredLight
		DebugLog "parrented"+Light
		If l\CullMesh=Light 
			xEntityParent(l\Parent,Parrent,1)
			DebugLog "parrented"
		EndIf 
	Next
End Function

Function DeferredLightColor(Light,R=128,G=128,B=128,LightAttenMultipler#=1)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			l\Red#  =Float(R)/Float(255)
			l\Green#=Float(G)/Float(255)
			l\Blue# =Float(B)/Float(255)
			l\LightAttenMultipler#=LightAttenMultipler#
		EndIf 
	Next
End Function

Function DeferredLightType(Light,LightType=1)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			l\LightType=LightType
		EndIf 
	Next
End Function

Function DeferredLightShadows(Light,Shadows=0,Optimise=1)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			l\Shadows=Shadows
			l\ShadowsDisableOp=Optimise
		EndIf 
	Next
End Function

Function DeferredLightState(Light,State=0)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			l\State=State
		EndIf 
	Next
End Function

Function GetLightAttenMultipler#(Light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			Return l\LightAttenMultipler#
		EndIf 
	Next
End Function

Function GetLightShadowsS(Light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			Return l\Shadows
		EndIf 
	Next
End Function

Function GetLightType(Light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			Return l\LightType
		EndIf 
	Next
End Function

Function GetLightRange(Light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			Return l\Radius
		EndIf 
	Next
End Function

Function GetScatter#(Light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			Return l\scatterpow#
		EndIf 
	Next
End Function

Function SetScatter(Light,scatterpow#)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			l\scatterpow#=scatterpow#
		EndIf 
	Next
End Function

Function GetLightR(Light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			Return l\Red#*255
		EndIf 
	Next
End Function

Function GetLightG(Light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			Return l\Green#*255
		EndIf 
	Next
End Function

Function GetLightB(Light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			Return l\Blue#*255
		EndIf 
	Next
End Function

Function DeferredFullBFX(Mesh)
	EntityTexture Mesh,DefaultFullBright,0,3
End Function

Function DeferredLightConeAngles(Light,SpotSize#=0.6,SpotSmoth#=20)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=Light Or l\Parent=Light
			l\SpotSize#=SpotSize#
			l\SpotSmoth#=SpotSmoth#
		EndIf 
	Next
End Function

Function DeleteDeferredLight(light)
	For l.DeferredLight= Each DeferredLight
		If l\CullMesh=light Or l\Parent=light
			xFreeEntity l\CullMesh
			xFreeTexture l\ShadowTexture
			Delete l
		EndIf 
	Next
End Function

Function FreeDeferredLights()
	For l.DeferredLight= Each DeferredLight
			xFreeEntity l\CullMesh
			xFreeTexture l\ShadowTexture
			Delete l 
	Next
End Function


;Recivers (Mesh)
Function AddDeferredReciver(Mesh,CastShadows=0)
	
	DR.DeferredReciver= New DeferredReciver
	DR\Mesh=Mesh
	DR\ShadowsState=CastShadows
	DR\Surface=0
	
	DebugLog "DR\Mesh"+DR\Mesh
	xSetEntityEffect DR\Mesh, DeferredMeshShader
	xSetEffectTechnique(DR\Mesh, "NormalMap")
	xEntityTexture DR\Mesh,DefaultNormal,0,1,0
	xEntityTexture DR\Mesh,DefaultSpecular,0,2
	xEntityTexture DR\Mesh,DefaultBright,0,3
	
End Function

Function LoadDeferredMesh( MeshPatch$,Shadows=0)

	;DeMesh=xLoadMeshWithChild(MeshPatch$)
	DeMesh=xLoadAnimMesh(MeshPatch$)
	
	If DeMesh
	For ChiCo=0 To xCountChildren(DeMesh)  
		DeChild=xGetChild(DeMesh,ChiCo)
		
		
		
		If DeChild<>0  
			DeSurfCou=xCountSurfaces(DeChild) 
			For DeSurfSel=0 To DeSurfCou
				DeSurf=xGetSurface( DeChild, DeSurfSel ) 
				If DeSurf<>0	
					SurfaceBrush=xGetSurfaceBrush(DeSurf) 
					SurfaceBrushTexture=xGetBrushTexture(SurfaceBrush,0) 
					SurfaceBrushTextureName$=xTextureName$(SurfaceBrushTexture)
					
					m.DeferredReciver= New DeferredReciver
					m\mesh=DeSurf
					m\ShadowsState=Shadows
					xSetSurfaceEffect DeSurf, DeferredMeshShader
					m\Surface=1
					xSurfaceTechnique (DeSurf, "NormalMap",0);NormalMap
					
					If SurfaceBrushTexture=0
						xSurfaceEffectTexture  DeSurf,"tAlbedo",DefaultTexture,0,0
					Else 
						xSurfaceEffectTexture  DeSurf,"tAlbedo",SurfaceBrushTexture ,0,0
					EndIf
					SurfaceBrushTextureName$=Lower(SurfaceBrushTextureName$)
					If Instr(SurfaceBrushTextureName$,".jpg")
						Normals$=Replace$(SurfaceBrushTextureName$,".jpg","_N.jpg")
						Spec$=Replace$(SurfaceBrushTextureName$,".jpg","_S.jpg")
						Emi$=Replace$(SurfaceBrushTextureName$,".jpg","_B.jpg")
						Relief$=Replace$(SurfaceBrushTextureName$,".jpg","_P.jpg")
					EndIf 
					
					If Instr(SurfaceBrushTextureName$,".png")
						Normals$=Replace$(SurfaceBrushTextureName$,".png","_N.png")
						Spec$=Replace$(SurfaceBrushTextureName$,".png","_S.png")
						Emi$=Replace$(SurfaceBrushTextureName$,".png","_B.png")
						Relief$=Replace$(SurfaceBrushTextureName$,".png","_P.png")
					EndIf 
					
					If Instr(SurfaceBrushTextureName$,".tga")
						Normals$=Replace$(SurfaceBrushTextureName$,".tga","_N.tga")
						Spec$=Replace$(SurfaceBrushTextureName$,".tga",".tga")
						Emi$=Replace$(SurfaceBrushTextureName$,".tga","_B.tga")
						Relief$=Replace$(SurfaceBrushTextureName$,".tga",".tga")
					EndIf 
					
					If Instr(SurfaceBrushTextureName$,".dds")
						;Normals$=Replace$(SurfaceBrushTextureName$,".dds","_N.dds")
						;Spec$=Replace$(SurfaceBrushTextureName$,".dds","_S.dds")
						;Emi$=Replace$(SurfaceBrushTextureName$,".dds","_B.dds")
						;Relief$=Replace$(SurfaceBrushTextureName$,".dds","_P.dds")
					EndIf 
					
					NormSize=xFileSize(Normals$)
					SpecSize=xFileSize(Spec$)
					EmiSize=xFileSize(Emi$)
					RelSize=xFileSize(Relief$) 
					
					If SurfaceBrushTexture<>0
					DebugLog SurfaceBrushTextureName$
					DebugLog "norm:"+Normals$+" Size is:"+NormSize
					DebugLog "spec:"+Spec$+" Size is:"+SpecSize
				EndIf 
					;xEntityTexture MapMesh,DropletBuff,0,1,0 
					If NormSize<>0
						xSurfaceEffectTexture  DeSurf,"tNormals",xLoadTexture(Normals$),0,0
					Else 
						xSurfaceEffectTexture  DeSurf,"tNormals",DefaultNormal,0,0;DefaultNormal
					EndIf 
					
					If SpecSize<>0
						xSurfaceEffectTexture  DeSurf,"tSpecular",xLoadTexture(Spec$),0,0
					Else 
						xSurfaceEffectTexture  DeSurf,"tSpecular",DefaultSpecular,0,0
					EndIf 
					
					If EmiSize<>0
						xSurfaceEffectTexture  DeSurf,"tBright",xLoadTexture(Emi$),0,0
					Else 
						xSurfaceEffectTexture  DeSurf,"tBright",DefaultBright,0,0
					EndIf 
					
					If DeferredParalax=1
						xSurfaceEffectBool(DeSurf, "EnableParalax", 1,0)
					If RelSize<>0
						xSurfaceEffectTexture  DeSurf,"tDepth",xLoadTexture(Relief$),0,0
					Else 
						xSurfaceEffectTexture  DeSurf,"tDepth",DefaultBright,0,0
					EndIf 
				EndIf 
					
				EndIf 
			Next 
		EndIf  
	Next 
Else 
	;ChatText("Wrong mesh:"+MeshPatch$,"red")
	DeMesh=xCreateCube()
EndIf 

	
	Return DeMesh
End Function

Function UpdateParalaxS()
	cxp=xEntityX(camera)
	cyp=xEntityY(camera)
	czp =xEntityZ(camera)
	For m.DeferredReciver= Each DeferredReciver
		If   m\Surface=1
			xSurfaceEffectVector(m\Mesh, "campos", cxp, cyp, czp, 0, 0)
		Else
			xSetEffectVector(m\Mesh, "campos", cxp, cyp, czp, 0, 0)
		EndIf 
	Next
End Function

Function SetDeferredMesh( DeMesh,Shadows=0)
	;DeMesh=xLoadMeshWithChild(MeshPatch$)
	
	DeChild=DeMesh
		If DeChild<>0  
			DeSurfCou=xCountSurfaces(DeChild) 
			For DeSurfSel=0 To DeSurfCou
				DeSurf=xGetSurface( DeChild, DeSurfSel ) 
				If DeSurf<>0	
					SurfaceBrush=xGetSurfaceBrush(DeSurf) 
					SurfaceBrushTexture=xGetBrushTexture(SurfaceBrush,0) 
					SurfaceBrushTextureName$=xTextureName$(SurfaceBrushTexture)
					
					m.DeferredReciver= New DeferredReciver
					m\mesh=DeSurf
					m\ShadowsState=Shadows
					xSetSurfaceEffect DeSurf, DeferredMeshShader
					m\Surface=1
					xSurfaceTechnique (DeSurf, "NormalMap",0);NormalMap
					
					DebugLog SurfaceBrushTexture+"   "+SurfaceBrushTextureName$
					If SurfaceBrushTexture=0
						xSurfaceEffectTexture  DeSurf,"tAlbedo",DefaultTexture,0,0
					Else 
						xSurfaceEffectTexture  DeSurf,"tAlbedo",SurfaceBrushTexture ,0,0
					EndIf
					
					SurfaceBrushTextureName$=Lower(SurfaceBrushTextureName$)
					If Instr(SurfaceBrushTextureName$,".jpg")
						Normals$=Replace$(SurfaceBrushTextureName$,".jpg","_N.jpg")
						Spec$=Replace$(SurfaceBrushTextureName$,".jpg","_S.jpg")
						Emi$=Replace$(SurfaceBrushTextureName$,".jpg","_B.jpg")
						Relief$=Replace$(SurfaceBrushTextureName$,".jpg","_P.jpg")
					EndIf 
					
					If Instr(SurfaceBrushTextureName$,".png")
						Normals$=Replace$(SurfaceBrushTextureName$,".png","_N.png")
						Spec$=Replace$(SurfaceBrushTextureName$,".png","_S.png")
						Emi$=Replace$(SurfaceBrushTextureName$,".png","_B.png")
						Relief$=Replace$(SurfaceBrushTextureName$,".png","_P.png")
					EndIf 
					
					If Instr(SurfaceBrushTextureName$,".tga")
						Normals$=Replace$(SurfaceBrushTextureName$,".tga","_N.tga")
						Spec$=Replace$(SurfaceBrushTextureName$,".tga","_S.tga")
						Emi$=Replace$(SurfaceBrushTextureName$,".tga","_B.tga")
						Relief$=Replace$(SurfaceBrushTextureName$,".tga","_P.tga")
					EndIf 
					
					If Instr(SurfaceBrushTextureName$,".dds")
						Normals$=Replace$(SurfaceBrushTextureName$,".dds","_N.dds")
						Spec$=Replace$(SurfaceBrushTextureName$,".dds","_S.dds")
						Emi$=Replace$(SurfaceBrushTextureName$,".dds","_B.dds")
						Relief$=Replace$(SurfaceBrushTextureName$,".dds","_P.dds")
					EndIf 
					NormSize=xFileSize(Normals$)
					SpecSize=xFileSize(Spec$)
					EmiSize=xFileSize(Emi$)
					RelSize=xFileSize(Relief$) 
					
					
					;xEntityTexture MapMesh,DropletBuff,0,1,0 
					If NormSize<>0
						xSurfaceEffectTexture  DeSurf,"tNormals",xLoadTexture(Normals$),0,0
					Else 
						xSurfaceEffectTexture  DeSurf,"tNormals",DefaultNormal,0,0;DefaultNormal
					EndIf 
					
					If SpecSize<>0
						xSurfaceEffectTexture  DeSurf,"tSpecular",xLoadTexture(Spec$),0,0
					Else 
						xSurfaceEffectTexture  DeSurf,"tSpecular",DefaultSpecular,0,0
					EndIf 
					
					If EmiSize<>0
						xSurfaceEffectTexture  DeSurf,"tBright",xLoadTexture(Emi$),0,0
					Else 
						xSurfaceEffectTexture  DeSurf,"tBright",DefaultBright,0,0
					EndIf 
					
					
					
					If DeferredParalax=1
						xSurfaceEffectBool(DeSurf, "EnableParalax", 1,0)
						If RelSize<>0
							xSurfaceEffectTexture  DeSurf,"tDepth",xLoadTexture(Relief$),0,0
						Else 
							xSurfaceEffectTexture  DeSurf,"tDepth",DefaultBright,0,0
						EndIf 
					EndIf
					
				EndIf 
			Next 
		EndIf  
		
	Return DeMesh
End Function

;Rendering World Witch Deferred
Function RenderWorldDeferred()
	
	For l.DeferredLight= Each DeferredLight
		xEntityParent l\CullMesh, 0
		If  l\Parent<>0
			xPositionEntity l\CullMesh,xEntityX#(l\Parent,1),xEntityY#(l\Parent,1),xEntityZ#(l\Parent,1)
			xRotateEntity(l\CullMesh,xEntityPitch(l\Parent,1),xEntityYaw(l\Parent,1),xEntityRoll(l\Parent,1),1)
		EndIf 
	Next
	
	;UpdateParalax()
	;Shadows Render
	If GlobalShadows=1	
		;UpdateDeferredShadows()
		UpdateDeferredOptimisedShadows()
	EndIf 
	xCls 
	;Fill MRT Buffer
    xSetMRT(DeferredAlbedo , 0, 0)
	xSetMRT(DeferredNormals, 0, 1)
	;xSetMRT(DeferredDepth  , 0, 2)
	;xSetMRT(DeferredAdvMaterials  , 0, 2)
	
	xWireframe Wire_Mode
    xRenderWorld 
;	xWireframe 0
	xUnSetMRT()

	
	
	If xKeyHit(5)
		xSaveBuffer(xTextureBuffer(DeferredAlbedo),"Diffuse.png")	
		xSaveBuffer(xTextureBuffer(DeferredNormals),"Normals.png")
		xSaveBuffer(xTextureBuffer(DeferredDepth),"DeferredDepth.png")
		
		;xSaveBuffer(xTextureBuffer(DeferredAdvMaterials),"Materials.png")
		;xSaveBuffer(xTextureBuffer(DeferredResult),"Result.png")
	EndIf 
	
	
	;xSetEffectVector (DeferredPoly),"vCamPos",xEntityX#(DeferredCamera,1),xEntityY#(DeferredCamera,1),xEntityZ#(DeferredCamera,1)
	
	
	
	
	
	;Render Lighting
	For l.DeferredLight= Each DeferredLight
		
		
		If l\State=1
	;		If xEntityDistance(DeferredCamera,l\CullMesh)<550
			If xEntityInView(l\CullMesh,Camera) ;And xEntityVisible(DeferredCamera,l\CullMesh) 
			;xShowEntity l\CullMesh
			xEntityParent l\CullMesh, LightPivot
					
			
			
			
			If l\Shadows=1 And GlobalShadows=1 
						xSetEffectInt l\CullMesh,"Shadows",1
						xSetEffectTexture 	l\CullMesh, 	"tCubeShadow", l\ShadowTexture
					Else 
						xSetEffectInt l\CullMesh,"Shadows",0
					EndIf 
					
					xSetEffectVector l\CullMesh,"vLightPos",xEntityX#(l\CullMesh,1),xEntityY#(l\CullMesh,1),xEntityZ#(l\CullMesh,1)
					xSetEffectVector l\CullMesh,"cLightColor",l\Red#,l\Green#,l\Blue#
					xSetEffectFloat  l\CullMesh,"fLightRadius",l\Radius
				
					xSetEffectInt l\CullMesh,"LightAttenMultipler",l\LightAttenMultipler#
					
					
					
					
					dist#=xEntityDistance(l\CullMesh,camera)/120
					If dist#>1
						dist#=1
					EndIf 
					
					
					xSetEffectFloat  l\CullMesh,"scatterpow",l\scatterpow#*dist#
					
					
					Select l\LightType
						Case 1 ;Omni
							xSetEffectTechnique(l\CullMesh, "DeferredLight")
							xSetEffectInt l\CullMesh,"LightAttenType",0
							xSetEffectVector l\CullMesh,"vLightAngles",l\SpotSize#,l\SpotSmoth#,-0.3
						Case 2 ;Spot
							xSetEffectTechnique(l\CullMesh, "DeferredLight")
							xSetEffectInt l\CullMesh,"LightAttenType",3
							xTFormNormal 0,0,1,l\CullMesh,0
							xSetEffectVector l\CullMesh,"vSpotLightDir",xTFormedX(),xTFormedY(),xTFormedZ()
							xSetEffectVector l\CullMesh,"vLightAngles",l\SpotSize#,l\SpotSmoth#,-0.3
							
						Case 3 ;line
							
							xSetEffectTechnique(l\CullMesh, "DeferredLight")
							xSetEffectInt l\CullMesh,"LightAttenType",1
							xSetEffectVector l\CullMesh,"vLightAngles",l\SpotSize#,l\SpotSmoth#,-0.3
							
							Antrad#=((l\Radius/2) +( l\Radius/(5+l\SpotSize#*16)))
							
							xPositionEntity l\Mesh2,xEntityX#(l\CullMesh,1),xEntityY#(l\CullMesh,1),xEntityZ#(l\CullMesh,1)
							xRotateEntity l\Mesh2,xEntityPitch#(l\CullMesh,1),xEntityYaw#(l\CullMesh,1),xEntityRoll#(l\CullMesh,1)
							xMoveEntity(l\Mesh2, 0, Antrad#, 0,0)
							
							xPositionEntity l\Mesh1,xEntityX#(l\CullMesh,1),xEntityY#(l\CullMesh,1),xEntityZ#(l\CullMesh,1)
							xRotateEntity l\Mesh1,xEntityPitch#(l\CullMesh,1),xEntityYaw#(l\CullMesh,1),xEntityRoll#(l\CullMesh,1)
							xMoveEntity(l\Mesh1, 0, -Antrad#, 0,0)
							
							
							
							
							
							xSetEffectVector l\CullMesh,"P0",xEntityX#(l\Mesh1,1),xEntityY#(l\Mesh1,1),xEntityZ#(l\Mesh1,1)
							
							xSetEffectVector l\CullMesh,"P1",xEntityX#(l\Mesh2,1),xEntityY#(l\Mesh2,1),xEntityZ#(l\Mesh2,1)
							
						Case 5
							xSetEffectTechnique(l\CullMesh, "DeferredAmbient")
							xSetEffectTexture 	l\CullMesh, "envTexture", EnvCube
							;Coming Soon
					End Select
					
					
					
					
					
					
					;xRenderEntity camera,l\CullMesh
				;	xHideEntity l\CullMesh
			;	EndIf	
				EndIf
		EndIf 
	Next	
	
	xSetBuffer (xTextureBuffer(DeferredResult))
	
	
	;xEntityParent DeferredPoly, LightPivot
	xShowEntity LightPivot
	xRenderEntity DeferredCamera,LightPivot
	xHideEntity LightPivot
	;xEntityParent DeferredPoly, 0
	xSetBuffer xBackBuffer()
	
	
	
	
	xSetEffectTexture DeferredPoly, "tLighting", DeferredResult 
	xSetEffectTechnique(DeferredPoly, "DeferredOutput")
	xRenderPostEffect(DeferredPoly)
	
	
	
	

		UpdateAlphaEntity()
		xStretchBackBuffer(DeferredResult, 0, 0, ScreenW, ScreenH, 0)
		
		
		
		
		If PostSSLR=1
			xStretchBackBuffer(OldTextureBuffer, 0, 0, OldTextureSize, OldTextureSize, 0)
			
			
			
			BlurTex(OldTextureBuffer,OldTextureBuffer,OldTextureSize,OldTextureSize)
		;BlurTex(OldTextureBuffer,OldTextureBuffer,OldTextureSize,OldTextureSize)
			
			xSetEffectTechnique(DeferredPoly, "DeferredSSLR")
			xSetEffectVector DeferredPoly,"Res", ScreenW, ScreenH, 1
			xSetEffectFloat  DeferredPoly,"ERRRFACTOR",ERRRFACTOR#
			xSetEffectTexture DeferredPoly, "OldTextureBuffer", OldTextureBuffer
			xRenderPostEffect(DeferredPoly)
			
			
			
			xSetBuffer xBackBuffer()
		EndIf 	
		
		If PostProcessing=1
			DrawEYEAdp ()
		EndIf 
		
		
		
		If PostFXAA=1
			For fxaalvln=0 To PostFXAALevels
				xStretchBackBuffer(PostFXAA_Texture, 0, 0, ScreenW, ScreenH, 0)
				xRenderPostEffects()
			Next
		EndIf 

		;If PostSSAO=1
			
			
			
			xStretchBackBuffer(PostSSAO_TexBlur,0,0,ScreenW,ScreenH,0);
			xSetEffectTechnique(PostSSAO_Poly,"SSAO");
			xRenderPostEffect(PostSSAO_Poly);
			
			
			
			If SSAO_BLUR=1
				xStretchBackBuffer(PostSSAO_Texture,0,0,xGraphicsWidth()/1,xGraphicsHeight()/1,0);
				xSetEffectTechnique(PostSSAO_Poly,"BlurSSAO");
				xSetEffectVector(PostSSAO_Poly,"TexelBlur",1,0,0,0);
				xRenderPostEffect(PostSSAO_Poly);
				
			EndIf
		;EndIf 
		
		RenderScattering()
		

	
	
End Function 







;Rendering World Witch Deferred
Function SimpleRenderDeferred(TexRender,siz=256)
	
	
	
	xCameraViewport DeferredCamera,0,0,siz,siz
    xSetMRT(DeferredAlbedo , 0, 0)
	xSetMRT(DeferredNormals, 0, 1)
	xWireframe Wire_Mode
    xRenderWorld 
	xUnSetMRT()
	
	xSetEffectVector (DeferredPoly),"vCamPos",xEntityX#(DeferredCamera,1),xEntityY#(DeferredCamera,1),xEntityZ#(DeferredCamera,1)
	
	
	
	xCameraViewport DeferredCamera,0,0,ScreenW, ScreenH
	
	
	xSetBuffer (xTextureBuffer(TexRender))
	xCls
	;Render Lighting
	For l.DeferredLight= Each DeferredLight
		xEntityParent l\CullMesh, 0
		
		If  l\Parent<>0
			xPositionEntity l\CullMesh,xEntityX#(l\Parent,1),xEntityY#(l\Parent,1),xEntityZ#(l\Parent,1)
			xRotateEntity(l\CullMesh,xEntityPitch(l\Parent,1),xEntityYaw(l\Parent,1),xEntityRoll(l\Parent,1),1)
		EndIf 
		
		
		
		
		If l\State=1
			;If xEntityDistance(DeferredCamera,l\CullMesh)<5500000
			;	If xEntityInView(l\CullMesh,DeferredCamera) ;And xEntityVisible(DeferredCamera,l\CullMesh) 
			;xShowEntity l\CullMesh
			xEntityParent l\CullMesh, LightPivot
			
			
			
			
			If l\Shadows=1 And GlobalShadows=1
				xSetEffectInt l\CullMesh,"Shadows",1
				xSetEffectTexture 	l\CullMesh, 	"tCubeShadow", l\ShadowTexture
			Else 
				xSetEffectInt l\CullMesh,"Shadows",0
			EndIf 
			
			xSetEffectVector l\CullMesh,"vLightPos",xEntityX#(l\CullMesh,1),xEntityY#(l\CullMesh,1),xEntityZ#(l\CullMesh,1)
			xSetEffectVector l\CullMesh,"cLightColor",l\Red#,l\Green#,l\Blue#
			xSetEffectFloat  l\CullMesh,"fLightRadius",l\Radius
			
			xSetEffectInt l\CullMesh,"LightAttenMultipler",l\LightAttenMultipler#
			
			
			
			Select l\LightType
				Case 1 ;Omni
					xSetEffectTechnique(l\CullMesh, "DeferredLight")
					xSetEffectInt l\CullMesh,"LightAttenType",0
				Case 2 ;Spot
					xSetEffectTechnique(l\CullMesh, "DeferredLight")
					xSetEffectInt l\CullMesh,"LightAttenType",1
					
					xTFormNormal 0,0,1,l\CullMesh,0
					xSetEffectVector l\CullMesh,"vSpotLightDir",xTFormedX(),xTFormedY(),xTFormedZ()
					xSetEffectVector l\CullMesh,"vLightAngles",l\SpotSize#,l\SpotSmoth#,-0.3
				Case 3 ;Directional
					
				Case 5
					xSetEffectTechnique(l\CullMesh, "DeferredAmbient")
					xSetEffectTexture 	l\CullMesh, "envTexture", EnvCube
							;Coming Soon
			End Select
			
			
			
			xSetEffectVector l\CullMesh,"Res", siz, siz, 1
			
					;xRenderEntity camera,l\CullMesh
			xHideEntity l\CullMesh
				;EndIf	
				;EndIf
		EndIf 
	Next	
	
	
	xSetEffectVector LightPivot,"Res", siz, siz, 1
	xShowEntity LightPivot
	
	xRenderEntity DeferredCamera,LightPivot
	xHideEntity LightPivot
	
	xSetBuffer xBackBuffer()
	
	
	xCameraViewport DeferredCamera,0,0,ScreenW, ScreenH
	
End Function 







Function DeleteDeferredMesh(mesh)
	For m.DeferredReciver= Each DeferredReciver
		If m\mesh=mesh
			xFreeEntity  m\mesh
			Delete m
		EndIf 
	Next 
End Function


Function DeactiveDeferredMesh(mesh)
	
	xHideEntity mesh
		
End Function

Function DeleteDeferredRecivers()
	For 	m.DeferredReciver= Each DeferredReciver
		Delete m
	Next	
End Function

;Shadows
Function UpdateDeferredShadows()
	For l.DeferredLight= Each DeferredLight
		RenderShadowCubemap(l\ShadowTexture,l\CullMesh,	l\Radius)
		For m.DeferredReciver= Each DeferredReciver
			If   m\Surface=1
				xSurfaceTechnique (m\Mesh, "NormalMap",0)
			Else
				xSetEffectTechnique(m\Mesh, "NormalMap")
			EndIf
		Next
	Next
End Function

Function UpdateDeferredOptimisedShadows()
	
	For l.DeferredLight= Each DeferredLight
			If  l\shadows=1
				If l\State=1
					If xEntityInView(l\CullMesh,DeferredCamera)
						If xEntityDistance(l\CullMesh,DeferredCamera)<290
							RenderShadowCubemap(l\ShadowTexture,l\CullMesh,	l\Radius)
							For m.DeferredReciver= Each DeferredReciver
								If   m\Surface=1
									xSurfaceTechnique (m\Mesh, "NormalMap",0)
								Else
									xSetEffectTechnique(m\Mesh, "NormalMap")
								EndIf 
							Next
						EndIf
					EndIf
				EndIf 
			EndIf 
Next
End Function

Function UpdateDeferredOptimisedShadows2()

For l.DeferredLight= Each DeferredLight
	If GlobalShadows=1
		If l\ShadowsDisableOp=1
			If  l\shadows=1
				If l\State=1
					If xEntityInView(l\CullMesh,DeferredCamera)
						If xEntityDistance(l\CullMesh,DeferredCamera)<290
							RenderShadowCubemap(l\ShadowTexture,l\CullMesh,	l\Radius)
							For m.DeferredReciver= Each DeferredReciver
								If   m\Surface=1
									xSurfaceTechnique (m\Mesh, "NormalMap",0)
								Else
									xSetEffectTechnique(m\Mesh, "NormalMap")
								EndIf 
							Next
						EndIf
					EndIf
				EndIf 
			EndIf 
		EndIf
	Else
		RenderShadowCubemap(l\ShadowTexture,l\CullMesh,	l\Radius)
		For m.DeferredReciver= Each DeferredReciver
			If   m\Surface=1
				xSurfaceTechnique (m\Mesh, "NormalMap",0)
			Else
				xSetEffectTechnique(m\Mesh, "NormalMap")
			EndIf 
		Next
	EndIf 
Next
End Function



Function RenderShadowCubemap(Texture%,  Entity,radius)
	
	For m.DeferredReciver= Each DeferredReciver
		If   m\Surface=1
			If 		m\ShadowsState=0
				If m\Mesh <>0
					xSurfaceTechnique (m\Mesh, "Depth",0) 
					xSurfaceEffectVector(m\Mesh, "vLightPos",-100000000000000,-100000000000000,-100000000000000,0,0) 
				EndIf 
			Else
				xSurfaceTechnique (m\Mesh, "Depth",0) 
				xSurfaceEffectVector(m\Mesh, "vLightPos",xEntityX#(Entity,1),xEntityY#(Entity,1),xEntityZ#(Entity,1),0,0) 
			EndIf 
		Else
			If 		m\ShadowsState=0
				xSetEffectTechnique(m\Mesh, "Depth") 
				xSetEffectVector(m\Mesh, "vLightPos",-100000000000000,-100000000000000,-100000000000000) 
			Else
			xSetEffectTechnique(m\Mesh, "Depth") 
			xSetEffectVector(m\Mesh, "vLightPos",xEntityX#(Entity,1),xEntityY#(Entity,1),xEntityZ#(Entity,1)) 
		EndIf
		EndIf 
	Next
	
	
	Local TexSiz% = xTextureWidth(Texture)
	xSetBuffer(xTextureBuffer(Texture))
	xCameraRange ShadowCamera,0.1,radius
	xShowEntity ShadowCamera
	xHideEntity DeferredCamera 
	
	xPositionEntity ShadowCamera,xEntityX#(Entity,1),xEntityY#(Entity,1),xEntityZ#(Entity,1),1
	xClsColor 255,255,255
	xCameraClsMode ShadowCamera,1,1
	xCameraViewport ShadowCamera,0,0,TexSiz,TexSiz
	xCls
	
	xSetCubeFace Texture,0
	xRotateEntity ShadowCamera,0,90,0
	xSetBuffer xTextureBuffer(Texture)
	xRenderWorld
	
	xSetCubeFace Texture,1
	xRotateEntity ShadowCamera,0,0,0
	xSetBuffer xTextureBuffer(Texture)
	xRenderWorld
	
	xSetCubeFace Texture,2
	xRotateEntity ShadowCamera,0,-90,0
	xSetBuffer xTextureBuffer(Texture)
	xRenderWorld
	
	xSetCubeFace Texture,3
	xRotateEntity ShadowCamera,0,180,0
	xSetBuffer xTextureBuffer(Texture)
	xRenderWorld
	
	xSetCubeFace Texture,4
	xRotateEntity ShadowCamera,-90,0,0
	xSetBuffer xTextureBuffer(Texture)
	xRenderWorld
	
	xSetCubeFace Texture,5
	xRotateEntity ShadowCamera,90,0,0
	xSetBuffer xTextureBuffer(Texture)
	xRenderWorld
	
	xShowEntity DeferredCamera
	xHideEntity ShadowCamera 
	
	xSetBuffer xBackBuffer()
	
End Function


Global VolPiv
Function LoadingAlphaSystem(szt=32)
	
	AlphaShader = xLoadFXFile("Deferred\DeferredParticles.fx")
	AlphaPivot = xCreatePivot()
	VolPiv= xCreatePivot()
	
	RefractionTexSize=szt
	RefractionTexture = xCreateTexture(szt, szt)
	
End Function

Function AddAlphaEntity(Mesh,Alpha#=1,Soft#=1,Modo=1,RenderMode=0)
	
	Ae.AlphaEntity = New AlphaEntity
	Ae\Mesh = Mesh
	Ae\Alpha#=Alpha#
	Ae\Soft#=Soft#
	Ae\Modo=Modo
	xSetEntityEffect Ae\Mesh, AlphaShader
	
	Select RenderMode
		Case 0
			xSetEffectTechnique Ae\Mesh, "Soft"	
		Case 1
			xSetEffectTechnique Ae\Mesh, "SoftBright"
		Case 2
			xSetEffectTechnique Ae\Mesh, "OnlyRender"
		Case 3
			xSetEffectTechnique Ae\Mesh, "Refraction"
		Case 4
			xSetEffectTechnique Ae\Mesh, "Decal"
	End Select		
	
	
	
	xSetEffectInt Ae\Mesh,"OnOff",Ae\Modo
	xSetEffectTexture Ae\Mesh,"DepthTexture",DeferredNormals
	xSetEffectTexture Ae\Mesh,	"sceneTexture", RefractionTexture
	xSetEffectTexture Ae\Mesh,	"envTexture", EnvCube;xLoadTexture("Deferred/textures/1ac1d923.dds",1 + 128)
	
	xSetEffectVector Ae\Mesh,"Res", ScreenW, ScreenH, 1
	xSetEffectVector Ae\Mesh,"CamPos",xEntityX(camera,1),xEntityY(camera,1),xEntityZ(camera,1),1
	xEntityParent Ae\Mesh, AlphaPivot
	xSetEffectVector Ae\Mesh,"EntityColor",1,1,1
	
End Function

Function AlphaEntityAlpha(Mesh,a#)
	For Ae.AlphaEntity = Each AlphaEntity
		If Mesh=Ae\Mesh
			Ae\Alpha#=a#
			xSetEffectFloat Ae\Mesh,"Alpha",a#
		EndIf 
	Next
End Function

Function DeleteAlphaEntity(Mesh)
	For Ae.AlphaEntity = Each AlphaEntity
		If Mesh=Ae\Mesh
			If Ae\Mesh<>0
				xFreeEntity Ae\Mesh
			EndIf 
			Delete Ae
		EndIf 
	Next
End Function


Function DeleteAllAlphaEntity()
	For Ae.AlphaEntity = Each AlphaEntity
		If Ae\Mesh<>0
			xFreeEntity Ae\Mesh
		EndIf 
			Delete Ae 
	Next
End Function

Function AlphaEntityColor(Mesh,rr,gg,bb)
	For Ae.AlphaEntity = Each AlphaEntity
		If Mesh=Ae\Mesh
			r#=(Float#( rr )/Float(255))/Float(1)
			g#=(Float#( gg )/Float(255))/Float(1)
			b#=(Float#( bb )/Float(255))/Float(1)
			xSetEffectVector Ae\Mesh,"EntityColor",r#,g#,b#
		EndIf 
	Next
End Function

Function UpdateAlphaEntity()
	
	xStretchBackBuffer(RefractionTexture, 0, 0, RefractionTexSize, RefractionTexSize, 0)
	xCameraClsMode DeferredCamera,0,1
	xCameraClsColor DeferredCamera,0,0,0
	
	For Ae.AlphaEntity = Each AlphaEntity
		
		If Ae\Mesh<>0
			xSetEffectFloat Ae\Mesh,"Alpha",Ae\Alpha#
			;xSetEffectVector Ae\Mesh,"CamPos",xEntityX(DeferredCamera,1),xEntityY(DeferredCamera,1),xEntityZ(DeferredCamera,1),1
			xSetEffectFloat Ae\Mesh,"Softness",Ae\Soft
			xSetEffectInt Ae\Mesh,"OnOff",Ae\Modo
			LientC=0
			
			
			If EnableParticleLighting=1
			If xEntityInView(Ae\Mesh,Camera) ;And xEntityVisible(DeferredCamera,l\CullMesh) 
				For l.DeferredLight= Each DeferredLight
					If l\State=1
						If l\LightType=1 Or 2
					If xEntityInView(l\CullMesh,Camera)	
						DistL2P=(xMeshHeight(Ae\Mesh)+xMeshWidth(Ae\Mesh))/2
						DistL2P=DistL2P+l\Radius
						
						If xEntityDistance(Ae\Mesh,l\CullMesh)<DistL2P
						
					xSetEffectVector  Ae\Mesh,"lightPosition["+LientC+"]",xEntityX#(l\CullMesh,1),xEntityY#(l\CullMesh,1),xEntityZ#(l\CullMesh,1)
					xSetEffectVector Ae\mesh,"lightColor["+LientC+"]",l\Red#,l\Green#,l\Blue#
					xSetEffectFloat  Ae\mesh,"lightRange["+LientC+"]",l\Radius
					LientC=LientC+1
				EndIf  
				EndIf 
				EndIf 
			EndIf 
			Next
			xSetEffectFloat  Ae\mesh,"lightCount",LientC
			xSetEffectFloat  Ae\mesh,"EnableLight",1
		EndIf 
		EndIf 
			
			
		Else 
			xFreeEntity Ae\Mesh
			Delete Ae
		EndIf 
		
	Next
	
	xShowEntity AlphaPivot
	xRenderEntity DeferredCamera ,AlphaPivot
	xHideEntity AlphaPivot
	xCameraClsMode DeferredCamera,1,1
	
	
	
	
End Function





Type ScaterringTable
	Field Mesh 
	Field Texture 
	Field Samples 
	Field LesnG#
End Type

Function DeleteAllScattering()
	For st.ScaterringTable = Each ScaterringTable
		xFreeTexture st\Texture
		;xFreeEntity st\Mesh
		Delete st
	Next
End Function

Function AddScatteringMesh(Mesh,LesnG#=0.8,Samples=32)
	
	st.ScaterringTable = New ScaterringTable
	st\Mesh = Mesh
	
	siz_w=w
	siz_h=h
	st\Texture=xCreateTexture(siz_w,siz_h) 
	st\LesnG#=LesnG#
	st\Samples=Samples
	
	xSetEntityEffect st\Mesh, AlphaShader
	xSetEffectTechnique st\Mesh, "SoftBright"
	xSetEffectTexture st\Mesh,"DepthTexture",DeferredNormals
	xSetEffectVector st\Mesh,"Res", w, h, 1
	xSetEffectVector st\Mesh,"EntityColor",1,1,1
	
End Function

Function RenderScattering()
	
	
	
	For st.ScaterringTable = Each ScaterringTable
		
		
		xCameraProject DeferredCamera,xEntityX(st\Mesh,1),xEntityY(st\Mesh,1),xEntityZ(st\Mesh,1)
		PX# = xProjectedX()/xGraphicsWidth()
		PY# = xProjectedY()/xGraphicsHeight()
		
		
		
		If PX > -1 And PX < 2 And PY > -1 And PY < 2 Then 
			
			
			
			xShowEntity st\Mesh
			xSetEffectFloat st\Mesh,"Alpha",1
			xSetEffectVector st\Mesh,"CamPos",xEntityX(DeferredCamera,1),xEntityY(DeferredCamera,1),xEntityZ(DeferredCamera,1),1
			xSetEffectFloat st\Mesh,"Softness",1
			xCameraClsMode DeferredCamera,0,1
			xCameraClsColor DeferredCamera,0,0,0
			xSetBuffer xTextureBuffer(st\Texture)
			xCls()
			xRenderEntity DeferredCamera ,st\Mesh
			xSetBuffer xBackBuffer()
			xHideEntity st\Mesh
			xCameraClsMode DeferredCamera,1,1
			
			
			xSetEffectFloat (DeferredPoly,"numSamples",st\Samples)
			xSetEffectFloat (DeferredPoly,"Leng",st\LesnG#)
			
			xSetEffectTexture DeferredPoly,"BlackTexture",st\Texture
			xSetEffectVector DeferredPoly,"SunPos",PX,PY,1
			xSetEffectTechnique(DeferredPoly, "Scattering")
			xRenderPostEffect(DeferredPoly)
			
		EndIf 
	Next
	
End Function





Global ScreenTex,BluredScreen,BluredScreen8,BluredScreenZZ,Droplets
Global HDRPoly ,HDRShader

Global cc_st# = -15, cc_r# = 255, cc_g# = 255, cc_b# = 255, cc_cn# = 12, cc_br# = 3

Function LoadHDR()
	
	Droplets=CreateDropletBuffer()
	
	
	
	ScreenTex = xCreateTexture(ScreenW, ScreenH,2) 
	BluredScreenZZ= xCreateTexture(ScreenW/2, ScreenH/2,2) 
	BluredScreen= xCreateTexture(512, 512,2048+2)
	BluredScreen8= xCreateTexture(32, 32,2048+2)
	
	HDRPoly = xCreatePostEffectPoly(camera, 1)
	HDRShader=xLoadFXFile("DeferredHDR.fx")
	
	
	xSetEntityEffect  HDRPoly,HDRShader
	xSetEffectTexture HDRPoly,"tScreen",ScreenTex
	xSetEffectTexture HDRPoly, "tGBNormals", DeferredNormals
	xSetEffectTexture HDRPoly,"tBlur",BluredScreen
	xSetEffectTexture HDRPoly,"tBlur8",BluredScreen8
	xSetEffectTexture 	HDRPoly, "tBightles",  DeferredAdvMaterials   
	
	DustTex=xLoadTexture(    "Deferred\textures\lensdirt.png")
	NoiseTexTex=xLoadTexture(    "Deferred\textures\noise.png")
	xSetEffectTexture HDRPoly,"dust",DustTex
	xSetEffectTexture HDRPoly,"Noise",NoiseTexTex
	
	
	
End Function





Function BlurTex(DestTex,StartTex,size,size2,Btech=0)
	xSetEffectTexture HDRPoly,"tScreen",StartTex
	
	
	If Btech=0
		xSetEffectTechnique HDRPoly,"Blur"
	Else 
		xSetEffectTechnique HDRPoly,"Blur2"
	EndIf 
	xRenderPostEffect(HDRPoly)
	xStretchBackBuffer(DestTex, 0, 0, size, size2, 0)
	xSetEffectTexture HDRPoly,"tScreen",ScreenTex
End Function



Function DrawEYEAdp ()
	xStretchBackBuffer(ScreenTex, 0, 0, ScreenW, ScreenH, 0)
	
	If MenuBlur=1
		For x=0 To 3
			BlurTex(ScreenTex,ScreenTex,ScreenW, ScreenH)
		Next
	EndIf 
	
	
	;BlurTex(BluredScreen,ScreenTex,512,512,1)
	
	;BlurTex(BluredScreen,BluredScreen,512,512)
	
	;BlurTex(BluredScreen8,ScreenTex,32,32,1)
	;BlurTex(BluredScreen8,BluredScreen8,16)
	
	;xSetEffectFloat HDRPoly, "RandomValue", Rnd(0,10)
	
;	xSetEffectTechnique HDRPoly,"HDR"
	;xRenderPostEffect(HDRPoly)
	;xStretchBackBuffer(BluredScreenZZ, 0, 0, ScreenW/2, ScreenH/2, 0)
	
;For xd=0 To 1
;		BlurTex(BluredScreenZZ,BluredScreenZZ,ScreenW/2, ScreenH/2)
;	Next
	
	xSetEffectTexture HDRPoly,"tBlur",BluredScreenZZ
	xSetEffectTechnique HDRPoly,"HDR2"
	xRenderPostEffect(HDRPoly)
	
End Function















;~IDEal Editor Parameters:
;~F#68#118#121#12B#136#13E#147#14F#157#15F#167#16F#177#17F#187#18F#197#19B#1A4#1AE
;~F#1B8#1C8#238#245#38F#3F9
;~C#Blitz3D