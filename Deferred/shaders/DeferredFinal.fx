


//##################  Varriables  ##################
const float4x4 g_mWorldViewProj	: MATRIX_WORLDVIEWPROJ;
const float4x4 g_mWorld			: MATRIX_WORLD;
const float4x4 g_mView 			: MATRIX_VIEWINVERSE;
const float4x4 g_mViewProjInv	: MATRIX_VIEWPROJINVERSE;
const float4x4 g_mViewProj		: MATRIX_VIEWPROJ;

float3 	vLightPos;
float3 	vLightPos2;
float3 	vSpotLightDir;
float3 vLightAngles;
float3 	vCamPos  : CAMERA_POSITION;;
float3 	cLightColor;
float 	fLightRadius;
float FallOfExp=1;
float LightAttenType;
float LightAttenMultipler=1;


float2 Res =(800,600)   ;

int Shadows 			= 0;
int LightType 			= 0;
int TexturedLight 		= 0;
float fShadowSmoth=1;


float3 P0 ;	// Control Point 0 (Start)
float3 P1;	// Control Point 1 (End)
float ERRRFACTOR=1;


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



const texture BlackTexture ;
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
    AddressU  = clamp;
    AddressV  = clamp;
    AddressW  = clamp;
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
};

//###############  Output VS  ##############
struct pisPP 
{
   float4 Pos    	: POSITION;
   float2 Tex	: TEXCOORD0;
   float3 Depth   		: TEXCOORD1;
};

void MainVS( in visPP IN, out pisPP OUT ) 
{
	OUT.Pos	= mul(IN.inPos ,g_mWorldViewProj);
	OUT.Tex	= IN.inTex;
	OUT.Depth 				= mul(IN.inPos, g_mWorldViewProj).xyw;
	//OUT.ProjTex = mul(IN.inPos ,LightProjMatrix);
}



//##################  PS  ##################

inline float4 EncodeDepthNormal( float fDepth, float3 vNormal )
{
	float4 cEncripted;
	cEncripted.xyz = vNormal.xyz;
	cEncripted.w = fDepth;
	return cEncripted;
}
inline void DecodeDepthNormal( float4 cEncripted, out float fDepth, out float3 vNormal )
{
	fDepth = cEncripted.w;
	vNormal = normalize(cEncripted.xyz);
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




inline float rand(float3 seed, int i) 
{
	float4 seed4 = float4(seed, i);
	float dot_product = dot(seed4, float4(12.9898, 78.233, 45.164, 94.673));
	return frac(sin(dot_product) * 43758.5453);
}

#define NUM_SAMP 4
#define SPREAD 0.01

inline float GetShadows(float3 vNormal,float3 vLightDir,float fLength,float3 vWorldPos)
{
float fShadow;
if ( Shadows == 1 ) {
  float nLight         = dot(vNormal,normalize(vLightDir));
 if(nLight > 0.0f)
	{
	//float vShadowSample = texCUBE(sCubeShadow, -vLightDir);
   	// fShadow = ((fLength-(2*(vShadowSample*0.04))) < vShadowSample) ? 1.0f : 0.0f;
   	
   	if ( fShadowSmoth == 1 ) {
   	   	for (int i = 0; i < NUM_SAMP; i++)
	{
		float vShadowSample = texCUBE(sCubeShadow, -vLightDir + (float3(rand(vWorldPos, i), rand(vWorldPos, i + 1), rand(vWorldPos, i + 2)) * SPREAD)).r;
		fShadow += ((fLength-3.5) < vShadowSample) ? 1.0f : 0.0f;
	}
	fShadow /=NUM_SAMP;
	
  } else {
	float vShadowSample = texCUBE(sCubeShadow, -vLightDir);
   	 fShadow = ((fLength-(2*(vShadowSample*0.04))) < vShadowSample) ? 1.0f : 0.0f;
   	}
	
}
  } else {
	fShadow=1;
  }
return fShadow;
}

float discardline=0.5;
float ConstantAtten = 0;
float LinearAtten = 0.1f;
float QuadraticAtten = 0;


float3 Bezier(float t)
{
	return (1 - t) * P0 + t * P1;
}

inline float GetLightAtten(float3 vNormal,float3 vWorldPos)
{
 float res;
if ( LightAttenType == 3 ) 
{
// Spot Light NEW code
    float3 vLightDir = vLightPos-vWorldPos.xyz;
   	float3 vLightDir2=vLightDir;
    float fLength = length(vLightDir);
    vLightDir = vLightDir / fLength;
  
 	 float fShadow=GetShadows( vNormal, vLightDir, fLength ,vWorldPos.xyz);
  
    float     fSpotAtten=0;
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
    NdL = lerp(NdL, 1.f, .25f); 
   res=res*NdL;
   
   //  res=res* max(0,dot(vNormal,vLightDir));
  	res = res+((saturate(1.0f - length(vLightDir2)/fLightRadius))*fShadow)*0.08; 
    
  
} 

if ( LightAttenType == 0 ) 
{
// If LightAttenType=0 then light is omni

	float3 vLightDir = vLightPos-vWorldPos.xyz;
	float fLength = length(vLightDir);
	vLightDir = vLightDir / fLength;
	float NdL = max(0,dot(vNormal,vLightDir));
   NdL = lerp(NdL, 1.f, .25f); 
    float fShadow=1;
 
    float rLight	= saturate(1.0f-(distance(vWorldPos.xyz,vLightPos)/fLightRadius));
	float3 vLight	= normalize(vLightPos-vWorldPos.xyz);
	float nLight	= dot(vLight,vNormal);
	float TempF=rLight+(nLight*rLight);

	if (TempF>0.0f) {
    fShadow=GetShadows( vNormal, vLightDir, fLength,vWorldPos.xyz);
    fShadow=fShadow*nLight;
    }
    
    float3 lightDirection = vLightPos - vWorldPos.xyz;
    float sqrDist = dot(lightDirection, lightDirection);
    res = saturate(fLightRadius/sqrDist-FallOfExp);
    res = (saturate(1.0f - length(lightDirection)/fLightRadius)+res)*fShadow; 

}


if ( LightAttenType == 1 ) 
{
// If LightAttenType=3 then light is tube

   LinearAtten = vLightAngles.x ;
   ConstantAtten= vLightAngles.y/190 ;
  // QuadraticAtten= vLightAngles.x ;

       float3 vLightDir = vLightPos-vWorldPos.xyz;

    float fLength = length(vLightDir);
    vLightDir = vLightDir / fLength;
  
 	 float fShadow=GetShadows( vNormal, vLightDir, fLength ,vWorldPos.xyz);
  
   
			// Find the nearest point on the path light from the
	// given pixel position in terms of t.
	float3 top = (P1 * P0) - (P1 *  vWorldPos.xyz) - pow(P0, 2) + (P0 *  vWorldPos.xyz);
	float3 bottom = 2 * P1 * P0 - pow(P1, 2) - pow(P0, 2);
	float t = ((top.z + top.y + top.x) / (bottom.z + bottom.y + bottom.x));
	
	// Calculate world position from this t value
	// Clamp 0 <= t <= 1, keeps the lighting position within the bounds of the path.
	float3 lightPos = Bezier(clamp(t, 0, 1));
	
	
	
	
	// Perform Phong lighting calculations using the light position.
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

float FogDensity=0.05;

float4 DeferredAmbientPS( in pisPP IN): COLOR0
{
	float fDepth;
	float3 vNormal;
	float4 vWorldPos;
	float2 TexCords;
	float3 cD;
	GetGbuffer( IN.Depth, TexCords, vNormal,vWorldPos ,fDepth,cD);
   cD=cD*cLightColor;
   
   
     	float Dist 				= distance(vWorldPos.xyz,vCamPos.xyz);
    	float Fog = saturate((Dist - 1) / (600 - 1));
  cD=lerp( cD, float3(0,0,0.1), Fog );
   //cD = mix(cD, float3(0,0,0.5), getFogFactor(fogParams, fFogCoord));
	return float4(cD,1);	
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


	float3 vRefl    = reflect ( -normalize(vCamPos-vWorldPos.xyz), vNormal );
	half  spec=(pow ( max ( dot (  vLightDir, vRefl ), 0.0 ), 64 )*(tex2D(sGBScreen,TexCords).a+0.1))*res;
  //half  spec  =((PhongSpecular(vNormal,-normalize(vWorldPos.xyz-vCamPos),65,normalize(vLightDir))*(tex2D(sBightles,TexCords).y+0.1))*res)*fShadow;
  //half  spec  =(  Spec(  vWorldPos,vNormal)*(tex2D(sBightles,TexCords).y+0.1))*res;

  //cLightColor.rgb =cLightColor.rgb* texCUBE(LightCubemapS, vLightDir);

  cD = (((cD) * (res+spec) * cLightColor.rgb));
  return float4(cD,1);    
}




float4 DeferredOutputPS( in pisPP IN): COLOR0
{
	float fDepth;
	float3 vNormal;
	float4 vWorldPos;
	float2 TexCords;
	float3 cD;
	GetGbuffer( IN.Depth, TexCords, vNormal,vWorldPos ,fDepth,cD);
	float3 vLighting=tex2D(sLighting,IN.Tex);
	vLighting=vLighting+tex2D(sBightles,IN.Tex).a*tex2D(sGBScreen,IN.Tex);

  
  	float Contrast=1.4;
	vLighting = vLighting - Contrast * (vLighting - 1.0f) * vLighting *(vLighting - 0.5f);
  

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


half GetFresnel(half NdotI, half bias, half power)
{
  half facing = (1.0 - NdotI);
  return bias + (1-bias)*pow(facing, power);
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
//PBCEM end

//float3 env = texCUBE(envMap, rdir);

    float attenuation = saturate(1.0f - length(CubemapPositionWS.xyz-vWorldPos.xyz)/BoxRad);
	float3 env = (( ( texCUBE(envSampler, rdir) - RefIntensity))*attenuation)*tex2D(sGBScreen,TexCords).a;
//float3 env = ((1.8 * ( texCUBE(envSampler, rdir) - 0.50))*attenuation)*tex2D(sGBScreen,TexCords).a;


	//cD=cD*attenuation;
	//cD=cD*env;
 	return float4(env,1);
} 

technique TEMP {
	pass p0 {
		vertexshader	= compile vs_3_0 MainVS();
		pixelshader		= compile ps_3_0 TEMP_PS();
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
		pixelshader		= compile ps_3_0 DeferredAmbientPS();
		
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

