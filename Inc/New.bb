
poly1 = xCreatePostEffectPoly(camera, 1)

screen2 = xCreateTexture(w, h,2) 
TONEMAP = xCreateTexture(1, 1,2048+2) 
blutS= xCreateTexture(512, 512,2048+2)

mPETextureExposureF=xCreateTexture(1,1,1+8+256)
mPETextureExposureS=xCreateTexture(1,1,1+8+256)
BlurTexture16=xCreateTexture(16,16) 
BlurTexture8=xCreateTexture(8,8) 
BlurTexture2=xCreateTexture(2,2) 
BlurTexture1=xCreateTexture(1,1) 


tvShader= xLoadFXFile("tvShader.fx")
xSetEntityEffect poly1, tvShader
xSetEffectTechnique(poly1, "MainTechnique")
xSetEffectTexture poly1, "SceneTexture", screen2





If xKeyHit(9) ps=1-ps
	
	If xKeyHit (8)
		
		
		tvShader= xLoadFXFile("tvShader.fx")
		xSetEntityEffect poly1, tvShader
		xSetEffectTechnique(poly1, "MainTechnique")
		xSetEffectTexture poly1, "SceneTexture", screen2
		
		
	EndIf 
	
	
	
	If ps=1
		
		xStretchBackBuffer(screen2, 0, 0, w, h, 0)
		xSetEffectFloat poly1, "random", Rnd(0.00,10.00)
		xSetEntityEffect poly1, tvShader
		xSetEffectTexture poly1, "SceneTexture", screen2
		xSetEffectTechnique(poly1, "MainTechnique")
		xRenderPostEffect(poly1)
		
		
		xStretchBackBuffer(screen2, 0, 0, w, h, 0)
		xSetEffectTechnique(poly1, "Bloom")
		xRenderPostEffect(poly1)
		
		
		xStretchBackBuffer(screen2, 0, 0, w, h, 0)
		xStretchBackBuffer(blutS, 0, 0, 512, 512, 0)
		xSetEffectTexture poly1, "BlurTexture", blutS
		xSetEffectTechnique(poly1, "Tresh")
		xRenderPostEffect(poly1)
		
		xStretchBackBuffer(blutS, 0, 0, 512, 512, 0)
		xSetEffectTechnique(poly1, "BlurH")
		xRenderPostEffect(poly1)
		
		xStretchBackBuffer(blutS, 0, 0, 512, 512, 0)
		xSetEffectTechnique(poly1, "BlurV")
		xRenderPostEffect(poly1)
		
		xStretchBackBuffer(blutS, 0, 0, 512, 512, 0)
		
		
		xCameraViewport Camera,0,0,1,1
		
		If mPEExposureLast
			xSetEffectTexture poly1,"SmallTexture",mPETextureExposureS
			xSetBuffer xTextureBuffer(mPETextureExposureF)
		Else
			xSetEffectTexture poly1,"SmallTexture",mPETextureExposureF
			xSetBuffer xTextureBuffer(mPETextureExposureS)
		EndIf
		
		xSetEffectTechnique(poly1, "Exp")
		xRenderPostEffect(poly1)
		
		If mPEExposureLast
			xSetEffectTexture poly1,"SmallTexture",mPETextureExposureF
		Else
			xSetEffectTexture poly1,"SmallTexture",mPETextureExposureS
		EndIf
		mPEExposureLast=Not mPEExposureLast
		
		xCameraViewport Camera,0,0,w,h
		xSetEffectTechnique(poly1, "Tonemap")
		xSetBuffer xBackBuffer()
		
		xRenderPostEffect(poly1)
		
		
	EndIf 
;~IDEal Editor Parameters:
;~C#Blitz3D