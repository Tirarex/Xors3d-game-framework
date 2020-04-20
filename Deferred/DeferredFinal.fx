


//##################  Varriables  ##################
const float4x4 g_mWorldViewProj	: MATRIX_WORLDVIEWPROJ;
const float4x4 g_mWorld			: MATRIX_WORLD;
const float4x4 g_mView 			: MATRIX_VIEWINVERSE;
const float4x4 g_mViewProjInv	: MATRIX_VIEWPROJINVERSE;
const float4x4 g_mViewProj		: MATRIX_VIEWPROJ;

float PI=3.14;

float3 	vCamPos  : CAMERA_POSITION;
float2  Res =(800,600);
float ERRRFACTOR=1;
float FogDensity=0.05;

float3 	vLightPos;
float3  P0 ;
float3  P1;
float3 	vSpotLightDir;
float3  vLightAngles;
float3 	cLightColor ;
float 	fLightRadius;
float   FallOfExp=1;
float   LightAttenType;
float   LightAttenMultipler=1;

float scatterpow=1;

float discardline=0.5;
float ConstantAtten = 0;
float LinearAtten = 0.1f;
float QuadraticAtten = 0;

#define NUM_SAMP 4
#define SPREAD 0.08
int Shadows 			= 0;
int LightType 			= 0;
int TexturedLight 		= 0;
float fShadowSmoth      = 1;

float3 CubemapPositionWS;
float3 BoxMax;
float3 BoxMin;
float BoxRad;
float RefIntensity=1;

const texture  tGBScreen;
const texture  tGBNormals;
const texture  tLighting;
const texture  tCubeShadow;
const texture  LightCubemapT;
const texture  tBightles;
const texture  envTexture;
const texture  OldTextureBuffer;
const texture  BlackTexture ;

sampler sOldTextureBuffer = sampler_state
{
    Texture = <OldTextureBuffer>;
       AddressU  = Border;
    AddressV  = Border;
    AddressW  = Border;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};



sampler BTexture = sampler_state {
    Texture   = <BlackTexture>;
    ADDRESSU  = WRAP;
    ADDRESSV  = WRAP;
    ADDRESSW  = WRAP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};

samplerCUBE LightCubemapS = sampler_state
{
    Texture = <LightCubemapT>;
    MinFilter = Linear;
    MagFilter = Linear;
    MipFilter = Linear;
    AddressU  = MIRROR;
    AddressV  = MIRROR;
    AddressW  = MIRROR;
    

};


samplerCUBE envSampler = sampler_state
{
    Texture = <envTexture>;
    MinFilter = Linear;
    MagFilter = Linear;
    MipFilter = Linear;
    AddressU  = BORDER;
    AddressV  = BORDER;
    AddressW  = BORDER;
};

sampler sBightles = sampler_state
{
	Texture = <tBightles>;
	ADDRESSU  = CLAMP;
    ADDRESSV  = CLAMP;
    ADDRESSW  = CLAMP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};


sampler sGBScreen = sampler_state
{
	Texture = <tGBScreen>;
	ADDRESSU  = CLAMP;
    ADDRESSV  = CLAMP;
    ADDRESSW  = CLAMP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};

sampler sGBNormals = sampler_state
{
	Texture = <tGBNormals>;
	ADDRESSU  = CLAMP;
    ADDRESSV  = CLAMP;
    ADDRESSW  = CLAMP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};

sampler sLighting = sampler_state
{
	Texture = <tLighting>;
	ADDRESSU  = CLAMP;
    ADDRESSV  = CLAMP;
    ADDRESSW  = CLAMP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};

samplerCUBE sCubeShadow = sampler_state
{
	Texture = <tCubeShadow>;
    ADDRESSU  = CLAMP;
    ADDRESSV  = CLAMP;
    MAGFILTER = ANISOTROPIC;
    MINFILTER = ANISOTROPIC;
    MIPFILTER = ANISOTROPIC;
};

//###############  Input VS  ###############
struct visPP 
{
   float4 inPos  : POSITION;
   float2 inTex  : TEXCOORD0;
      float3 Normal : NORMAL;
};

//###############  Output VS  ##############
struct pisPP 
{
   float4 Pos    	: POSITION;
   float2 Tex		: TEXCOORD0;
   float3 Depth   	: TEXCOORD1;
   	float3 NormalSp : TEXCOORD2;
   		float3 SpecPW : TEXCOORD3;
};

void MainVS( in visPP IN, out pisPP OUT ) 
{
	OUT.NormalSp		= normalize(mul(IN.Normal, g_mWorld));
	OUT.SpecPW	= mul(IN.inPos, g_mWorld).xyz;	
		
	OUT.Pos	= mul(IN.inPos ,g_mWorldViewProj);
	OUT.Tex	= IN.inTex;
	OUT.Depth 				= mul(IN.inPos, g_mWorldViewProj).xyw;
}

inline float Luminance( float3 c )
{
	return dot( c, float3(1.0, 1.0, 1.0) );
}





inline void GetGbuffer( float3 SSDepth, out float2 TexCords, out float3 vNormal,out float4 vWorldPos ,out float fDepth,out float3 cD)
{
    SSDepth.xz 			= -SSDepth.xz;
	float2 texelSize	= float2(1.0 / Res.x, 1.0 / Res.y );
	TexCords     = (0.5 * SSDepth.xy / SSDepth.z + 0.5) + ( texelSize / 2.0 );
	
    cD =tex2D(sGBScreen, TexCords);

    
	float4 NormalsAndDepth = tex2D(sGBNormals,TexCords);
	fDepth = NormalsAndDepth.w;
	vNormal = normalize(NormalsAndDepth.xyz);


	vWorldPos.x = TexCords.x * 2.0f - 1.0f;
	vWorldPos.y = -(TexCords.y * 2.0f - 1.0f);
	vWorldPos.z = fDepth;
	vWorldPos.w = 1.0f;
	vWorldPos = mul(vWorldPos, g_mViewProjInv);
    vWorldPos /= vWorldPos.w;
}




float4 DeferredAmbientPS( in pisPP IN): COLOR0
{
	float fDepth;
	float3 vNormal;
	float4 vWorldPos;
	float2 TexCords;
	float3 cD;
	GetGbuffer( IN.Depth, TexCords, vNormal,vWorldPos ,fDepth,cD);
    cD=cD*cLightColor;
  
    float Dist= distance(vWorldPos.xyz,vCamPos.xyz);
    float Fog = saturate((Dist - 50) / (460 - 1));
        float fogx = clamp(distance(vWorldPos,vCamPos)/825.0,0.0,10.0);
    cD=lerp( cD, float3(0.1,0.1,0.1), Fog);

	return float4(cD.xyz,1);	
		
}



float InScatter(float3 start, float3 dir, float3 lightPos, float d)
{
	float3 q = start - lightPos;
	float b = dot(dir, q);
	float c = dot(q, q);
	float s = 1.0f / sqrt(c - b*b);
	float l = s * (atan( (d + b) * s) - atan( b*s ));
	return l;	
}


inline float rand(float3 seed, int i) 
{
	float4 seed4 = float4(seed, i);
	float dot_product = dot(seed4, float4(12.9898, 78.233, 45.164, 94.673));
	return frac(sin(dot_product) * 43758.5453);
}

inline float GetShadows(float3 vNormal,float3 vLightDir,float fLength,float3 vWorldPos)
{
float fShadow;
if ( Shadows == 1 ) {
  float nLight         = dot(vNormal,normalize(vLightDir));
 if(nLight > 0.0f)
	{
   	if ( fShadowSmoth == 1 ) {
   	   	for (int i = 0; i < NUM_SAMP; i++)
	{
		float vShadowSample = texCUBE(sCubeShadow, -vLightDir + (float3(rand(vWorldPos, i), rand(vWorldPos, i + 1), rand(vWorldPos, fLength)) * SPREAD)).r;
		fShadow += ((fLength-3.5) < vShadowSample) ? 1.0f : 0.0f;
	}
	fShadow /=NUM_SAMP;
	
  } else {
	float vShadowSample = texCUBE(sCubeShadow, -vLightDir);
	fShadow = ((fLength-1.5) < vShadowSample) ? 1.0f : 0.0f;
   //	 fShadow = ((fLength-(2*(vShadowSample*0.04))) < vShadowSample) ? 1.0f : 0.0f;
   	}
}
  } else {
	fShadow=1;
  }
return fShadow;
}

float4 CalculatePointLight( float3 normal,  float3 position,  float3 light,  float shininess ,float3 Diff)
{
 //Direction towards the light
 float3 L = light.xyz - position;
 
 //Distance between the surface and the point light
 float distance = length(L);
 
 //If the current pixel is outside of the range of influence, discard it
 if(distance > fLightRadius)
 {
     discard;
 }
 
 float attenuation = 1.0;
 vLightAngles.x=vLightAngles.x*9;
 //If the distance is greater than the attenuation threshold
 if(distance > vLightAngles.x )
 {
     //Calculate the attenuation factor
     attenuation = saturate(1 - (distance - vLightAngles.x ) / (fLightRadius - vLightAngles.x ));
 }
 
 float3 vLightDir = light.xyz-position.xyz;
 float fShadow=GetShadows( normal, vLightDir, length(vLightDir) ,position.xyz);
 	attenuation=attenuation*fShadow;
 
 L = normalize(L);
 
 float3 diffuse = (cLightColor / PI);
 
 //View vector from the camera to the surface being rendered
 float3 E = normalize(vCamPos - position); 
 
 //Halfway vector 
 float3 H = normalize(L + E);
 
 //Specular color
 float3 Ks = float3(Diff);
 
 //Distribution term
 float3 D = ((shininess + 2) / (2 * PI)) * (pow(max(0.01, dot(normal, H)),  shininess));
 
 //Fresnel term
 float3 F = Ks + ((1 - Ks) * (pow(1 - (max(0.0, dot(L, H))), 5)));
 
 //Shadowing term
 float3 G = 1 / pow(max(0.0001, dot(L, H)), 2);
 
 float3 BRDF = (F * G * D) / 4;
 
 float3 lightOut = (diffuse + BRDF) * (max(0.0, dot(normal, L)) * (cLightColor * PI));
 
 return float4 (lightOut * attenuation, 1.0);
 }




float3 Bezier(float t)
{
	return (1 - t) * P0 + t * P1;
}




inline float GetLightAtten(float3 vNormal,float3 vWorldPos)
{
 float res;
 float3 vLightDir = vLightPos-vWorldPos.xyz;
 
	float3 lightDirection = vLightDir;
 float fLength = length(vLightDir);
 vLightDir = vLightDir / fLength;
 
 
if ( LightAttenType == 0 ) 
{
	half fNDotL				=  dot( vNormal, normalize(vLightDir) );						// N.L
	fNDotL = lerp(fNDotL, 1.f, .25f);
	if( fNDotL > 0.0f )
	{
	
		half3 fvPixelToViewer	= normalize( -vWorldPos.xyz );															// V
		half3 fvReflection		= normalize( 2.0f * fNDotL * vNormal -  normalize(vLightDir) );			// R
		 float  fShadow  =GetShadows( vNormal, vLightDir, fLength,vWorldPos.xyz)*fNDotL;
		// crude attenuation
		half fDistanceToLight = length( lightDirection );
		res = max( 0, 1 - ( fDistanceToLight / fLightRadius ))*fShadow;	
		//res=1-( 1/((fDistanceToLight-fLightRadius)+1));
		
	}
}
 
 
  
if ( LightAttenType == 3 ) 
{
	half fNDotL				=  dot( vNormal, normalize(vLightDir) );	// N.L
	res=0;
	float fShadow=0;
	if( fNDotL > 0.0f )
	{
 	 fShadow=GetShadows( vNormal, vLightDir, fLength ,vWorldPos.xyz);
 	
 	
    float fSpotAtten=0;
    float fDistance = distance( vWorldPos.xyz, vLightPos );
    float fLinearAtten = lerp( 1.0f, 0.0f, fDistance / fLightRadius );
 
    float3 vLight = normalize( vLightPos - vWorldPos.xyz);
    float cosAlpha      = max( 0.0f, dot( vLight, -vSpotLightDir) );
    if( cosAlpha > vLightAngles.x )
    {
        fSpotAtten = 1.0f;
    }
    else if( cosAlpha > vLightAngles.z )
    {
        fSpotAtten = pow( (cosAlpha - vLightAngles.z) / (vLightAngles.x - vLightAngles.z), vLightAngles.y );
    }
    res = fLinearAtten * fSpotAtten;
    res=res* fShadow;
    
    float NdL = max(0,dot(vNormal,vLightDir));
    //NdL = lerp(NdL, 1.f, .25f); 
    res=res*NdL;
 // 	res = res+((saturate(1.0f - length(lightDirection)/fLightRadius))*fShadow)*0.08; 
    }
  
} 


if ( LightAttenType == 1 ) 
{
    LinearAtten = vLightAngles.x ;
    ConstantAtten= vLightAngles.y/190 ;
 	float fShadow=GetShadows( vNormal, vLightDir, fLength ,vWorldPos.xyz);
	float3 top = (P1 * P0) - (P1 *  vWorldPos.xyz) - pow(P0, 2) + (P0 *  vWorldPos.xyz);
	float3 bottom = 2 * P1 * P0 - pow(P1, 2) - pow(P0, 2);
	float t = ((top.z + top.y + top.x) / (bottom.z + bottom.y + bottom.x));
	float3 lightPos = Bezier(clamp(t, 0, 1));
	float distance = distance(lightPos,  vWorldPos.xyz);
	
	float3 L = normalize(vLightPos -  vWorldPos.xyz);
	float3 V = normalize(vCamPos -  vWorldPos.xyz);
	float3 N = normalize(vNormal);
	float3 R = normalize(reflect(-L, N));
	
	float specular = pow(saturate(dot(R, V)), 55);
	res = 1 / (ConstantAtten + (LinearAtten * distance) + (QuadraticAtten * pow(distance, 2)));
	res=(res*2)-discardline;
	res=res*fShadow;
}
return res*LightAttenMultipler;
}


float4 DeferredLighPS( in pisPP IN): COLOR0
{
	float fDepth;
	float3 vNormal;
	float4 vWorldPos;
	float2 TexCords;
	float3 cD;
	GetGbuffer( IN.Depth, TexCords, vNormal,vWorldPos ,fDepth,cD);


	float3 vLightDir = vLightPos-vWorldPos.xyz;
	float fLength = length(vLightDir);
	vLightDir = vLightDir / fLength;
	float   res =GetLightAtten( vNormal, vWorldPos);
	


    float3 dir =  vWorldPos.xyz - vCamPos;// + noise;
      
	float3 vRefl    = reflect ( normalize(dir), vNormal );
	//half  spec=(pow ( max ( dot (  vLightDir, vRefl ), 0.0 ), 128 )*(tex2D(sGBScreen,TexCords).a+0.1))*res;
    half spec=(pow ( max ( dot ( vLightDir, vRefl ), 0.0 ), 128)*tex2D(sGBScreen,TexCords).a)*res;

	float scatter=0;
	float scattercolor ;
	float AttenPow=0;
	
	if (scatterpow > 0){
	float l = length(dir);
	dir /= l;
	float3 PosCam		= normalize(IN.SpecPW.xyz-vCamPos);
	AttenPow	= 1-pow(1.0f-saturate(dot(PosCam,IN.NormalSp)),1);
    scatter = (InScatter(vCamPos, dir, vLightPos, l) *scatterpow);
      scatter=scatter*AttenPow;
	//scatter=saturate(scatter);
	  scattercolor = saturate(cLightColor*scatter);
	}
	
	




  cD = (((cD) * (res) * cLightColor.rgb));
 return float4(cD+(cLightColor*spec)+ scattercolor,1);

 
  //return float4( cD*CalculatePointLight( vNormal,  vWorldPos.xyz,  vLightPos, tex2D(sGBScreen,TexCords).a,cD.xyz),1);  
}




float4 DeferredOutputPS( in pisPP IN): COLOR0
{
	float3 vLighting=tex2D(sLighting,IN.Tex);
	vLighting=vLighting+tex2D(sBightles,IN.Tex).x*tex2D(sGBScreen, IN.Tex);
	float shad=tex2D(sLighting,IN.Tex).w;
	return  float4(vLighting,1) ;
}




float4 TEMP_PS( in pisPP IN): COLOR0
{
	return float4(0,0,0,0);
}


float3 GetPosition(float2 UV, float depth)
{
    float4 position = 1.0f; 
    position.x = UV.x * 2.0f - 1.0f; 
    position.y = -(UV.y * 2.0f - 1.0f); 
    position.z = depth; 
    position = mul(position, g_mViewProjInv); 
    position /= position.w;
    return position.xyz;
}


float3 GetUV(float3 position)
{
     float4 pVP = mul(float4(position, 1.0f), g_mViewProj);
     pVP.xy = float2(0.5f, 0.5f) + float2(0.5f, -0.5f) * pVP.xy / pVP.w;
     return float3(pVP.xy, pVP.z / pVP.w);
}

float GetDepth(float2 nuv)
{
     return  tex2D(sGBNormals,nuv.xy).w;
}


float Leng = 0.5f;
float Decay = 0.99F;
float Exposition = 1.0f;
int numSamples = 16;
float2 SunPos;

float4 psScattering( float2 TexCoords : TEXCOORD0 ) : COLOR {
	float2 DeltaTex = TexCoords - SunPos.xy; 
	float2 NewUv = TexCoords;
	float3 Scatter;
	float FallOff = 1.0;	
	DeltaTex *= ( 1.0f / numSamples ) * Leng; 
	
	

   	for ( int i = 0; i < numSamples; i++ ) {		
		
		NewUv -= DeltaTex;
		Scatter.r += tex2D( BTexture, NewUv+0.001 ).r * FallOff;	
		Scatter.g += tex2D( BTexture, NewUv ).g * FallOff;
		Scatter.b += tex2D( BTexture, NewUv-0.001 ).b * FallOff;
		FallOff *= Decay;
	}
	
	Scatter /= numSamples;
   	return float4(Scatter * Exposition, 1);
}



float4 DeferredSSLRPS(float2 texCoords : TEXCOORD0): COLOR0
{

	float3 vNormal;
	float3 vWorldPos;
	float2 TexCords;
	float3 cD;
	
	vWorldPos.xyz=GetPosition(texCoords,  GetDepth(texCoords));
	float4 NormalsAndDepth = tex2D(sGBNormals,texCoords);
	
	vNormal = normalize(NormalsAndDepth.xyz);
	float3 viewDir = normalize(vWorldPos.xyz - vCamPos);
	float3 reflectDir = normalize(reflect(viewDir, vNormal));
	
	float3 currentRay = 0;
	float error;
	float3 nuv = 0;
	float  L = 1;

	for(int i = 0; i < 10; i++)
	{
	    currentRay = vWorldPos.xyz + reflectDir * L;
	    nuv = GetUV(currentRay); // проецирование позиции на экран
	    float n = GetDepth(nuv.xy); // чтение глубины из DepthMap по UV
     	float3 newPosition = GetPosition(nuv.xy, n);
	    L = length(vWorldPos.xyz - newPosition);
	}

	 L = L * ERRRFACTOR; //0.007
	 error = (1 - L);
	 error=saturate(error);
   	 float fresnel = 0.0 + 2.8 * pow(1+dot(viewDir, vNormal), 2);
   	 fresnel=saturate(fresnel);
     float refstrea=(fresnel*error);

	 float3 reflection=((tex2D(sOldTextureBuffer, nuv).rgb*refstrea)*tex2D(sGBScreen,texCoords).a)*0.8;
	 float Contrast=2.4;
	 reflection = saturate(reflection - Contrast * (reflection - 1.0f) * reflection *(reflection - 0.5f));
	
	 cD= tex2D(sLighting, texCoords)+reflection;
 
  
  
	return float4(cD,1);	
}



 float4 DeferredCubemapPS( in pisPP IN): COLOR0
{
	float fDepth;
	float3 vNormal;
	float4 vWorldPos;
	float2 TexCords;
	float3 cD;
	GetGbuffer( IN.Depth, TexCords, vNormal,vWorldPos ,fDepth,cD);

	float3 dir = normalize(vWorldPos - vCamPos);
    float3 rdir = reflect(dir, vNormal);
	
	//BPCEM
	float3 nrdir = normalize(rdir);
	float3 rbmax = (BoxMax - vWorldPos)/nrdir;
	float3 rbmin = (BoxMin - vWorldPos)/nrdir;
	
	float3 rbminmax = (nrdir>0.0f)?rbmax:rbmin;
	float fa = min(min(rbminmax.x, rbminmax.y), rbminmax.z);
	
	float3 posonbox = vWorldPos + nrdir*fa;
	rdir = posonbox - CubemapPositionWS;

    float attenuation = saturate(1.0f - length(CubemapPositionWS.xyz-vWorldPos.xyz)/BoxRad);
	float3 env = (( ( texCUBE(envSampler, rdir) - RefIntensity))*attenuation)*tex2D(sGBScreen,TexCords).a;
 	return float4(env,1);
} 

technique TEMP {
	pass p0 {
		vertexshader	= compile vs_3_0 MainVS();
		pixelshader		= compile ps_3_0 TEMP_PS();
	}
}

float4 show_stencil_ps_main( in pisPP IN): COLOR0
{

	return float4(1,0,0,1);	
}

technique DeferredLight1
{
	pass p0
	{
		VertexShader		= compile vs_2_0 MainVS();
        PixelShader			= null;
        
        CullMode			= none;
        ColorWriteEnable	= 0x0;        
        
        // Disable writing to the frame buffer
        AlphaBlendEnable	= true;
        SrcBlend			= Zero;
        DestBlend			= One;
        
        // Disable writing to depth buffer
        ZWriteEnable		= false;
        ZEnable				= true;
        ZFunc				= Less;
       
        // Setup stencil states
        StencilEnable		= true;
        TwoSidedStencilMode = true;
        
        StencilRef			= 1;
        StencilMask			= 0xFFFFFFFF;
        StencilWriteMask	= 0xFFFFFFFF;
        
        // stencil settings for front facing triangles
        StencilFunc			= Always;
        StencilZFail		= Incr;
        StencilPass			= Keep;
        
        // stencil settings for back facing triangles
        Ccw_StencilFunc		= Always;
        Ccw_StencilZFail	= Decr;
        Ccw_StencilPass		= Keep;
	}
	
	pass p1
	{
		VertexShader	= compile vs_3_0 MainVS();
		PixelShader		= compile ps_3_0 DeferredLighPS();
		
		ZEnable			= false;
		ZWriteEnable	= false;
							
		AlphaBlendEnable = true;		
		SrcBlend		= One;
        DestBlend		= One;
        
        // draw backfaces so that we're always guaranteed to get the right behaviour when inside the light volume
        CullMode		= CW;	        
        
        ColorWriteEnable = 0xFFFFFFFF;

  		StencilEnable	= true;
  		TwoSidedStencilMode = false;
        StencilFunc		= Equal;

		StencilFail		= Keep;
		StencilZFail	= Keep;
		StencilPass		= Keep;

		StencilRef		= 0;
		StencilMask		= 0xFFFFFFFF;
        StencilWriteMask = 0xFFFFFFFF;
	}
	
	
	
	
	
}


technique DeferredLight {
	pass p0 {
		vertexshader	= compile vs_3_0 MainVS();
		pixelshader		= compile ps_3_0 DeferredLighPS();
	AlphaBlendEnable = true;
		SrcBlend = One;
		DestBlend = One;
		
		ZWriteEnable=false;
		ZEnable=True;
		CullMode=CW;
		
		StencilEnable=true;
		StencilFunc=Always;
		
		StencilFail=Keep;
		StencilZFail=Incr;
		StencilPass=Keep;
		
		StencilRef=1;
		StencilMask=1;

	}
}


technique DeferredOutput {
	pass p0 {
		vertexshader	= compile vs_3_0 MainVS();
		pixelshader		= compile ps_3_0 DeferredOutputPS();
	}
}


technique DeferredSSLR {
	pass p0 {
		vertexshader	= compile vs_3_0 MainVS();
		pixelshader		= compile ps_3_0 DeferredSSLRPS();
	}
}



technique DeferredAmbient {
	pass p0 {
		vertexshader	= compile vs_3_0 MainVS();
		pixelshader		= compile ps_3_0 DeferredAmbientPS ();
		AlphaBlendEnable = true;
		SrcBlend = One;
		DestBlend = One;
		
		ZWriteEnable=false;
		ZEnable=True;
		CullMode=CW;
		
		StencilEnable=true;
		StencilFunc=Always;
		
		StencilFail=Keep;
		StencilZFail=Incr;
		StencilPass=Keep;
		
		StencilRef=1;
		StencilMask=1;
	}
}



technique Scattering {
	pass p0 {
		//vertexshader	= compile vs_2_0 vs();
		pixelshader		= compile ps_3_0 psScattering();
		AlphaBlendEnable = true;
		SrcBlend = One;
		DestBlend = One;
	}
}



technique DeferredCubemap {
	pass p0 {
		vertexshader	= compile vs_3_0 MainVS();
		pixelshader		= compile ps_3_0 DeferredCubemapPS();
	AlphaBlendEnable = true;
		SrcBlend = One;
		DestBlend = One;
		
		ZWriteEnable=false;
		ZEnable=True;
		CullMode=CW;
		
		StencilEnable=true;
		StencilFunc=Always;
		
		StencilFail=Keep;
		StencilZFail=Incr;
		StencilPass=Keep;
		
		StencilRef=1;
		StencilMask=1;
	}
}

