

//##################  Varriables  ##################
const float4x4 g_mWorldViewProj	: MATRIX_WORLDVIEWPROJ;
const float4x4 g_mWorld			: MATRIX_WORLD;
const float4x4 g_mView 			: MATRIX_VIEWINVERSE;
const float4x4 g_mViewProjInv	: MATRIX_VIEWPROJINVERSE;
texture tAlbedo   : TEXTURE_0;
texture tNormals  : TEXTURE_1;
texture tSpecular : TEXTURE_2;
texture tBright : TEXTURE_3;
texture tDepth : TEXTURE_4;
float4 camposG : CAMERA_POSITION;
float3 vLightPos;
bool EnableParalax=1;

sampler sDepth = sampler_state
{
	Texture	  = <tDepth>;
	AddressU  = WRAP;
	AddressV  = WRAP;
	AddressW  = WRAP;
    MagFilter = Anisotropic;
    MinFilter = Anisotropic;
    MipFilter = Anisotropic;
    MaxAnisotropy = 4;
};

sampler2D sBright = sampler_state
{
	Texture	  = <tBright>;
	ADDRESSU  = WRAP;
	ADDRESSV  = WRAP;
	ADDRESSW  = WRAP;
	MAGFILTER = LINEAR;
	MINFILTER = LINEAR;
	MIPFILTER = LINEAR;
};

 
sampler sDiffuse = sampler_state
{
	Texture	  = <tAlbedo>;
	AddressU  = WRAP;
	AddressV  = WRAP;
	AddressW  = WRAP;
    MagFilter = Anisotropic;
    MinFilter = Point;
    MipFilter = Point;
    MaxAnisotropy = 4;
};

sampler sNormals = sampler_state
{
	Texture	  = <tNormals>;
	AddressU  = WRAP;
	AddressV  = WRAP;
	AddressW  = WRAP;
    MagFilter = Anisotropic;
    MinFilter = Anisotropic;
    MipFilter = Anisotropic;
};

sampler sSpecular = sampler_state
{
	Texture	  = <tSpecular>;
	AddressU  = WRAP;
	AddressV  = WRAP;
	AddressW  = WRAP;
    MagFilter = Anisotropic;
    MinFilter = Anisotropic;
    MipFilter = Anisotropic;
};
//##################  Input VS  ##################
struct sv_TBN_TC {
	float4 vPos			: POSITION0;
	float3 vNormal    	: NORMAL;
	float3 vTangent   	: TANGENT;
	float3 vBinormal  	: BINORMAL;
	float2 vTexCoords	: TEXCOORD0;
	float2 vTexCoords1	: TEXCOORD1;
};
//##################  Output VS  ##################
struct sp_PW_TBN_TC {
	float4 vPos			: POSITION0;
	float4 pWorld		: TEXCOORD0;
	float3 vNormal      : TEXCOORD1;
	float3 vTangent   	: TEXCOORD2;
	float3 vBinormal  	: TEXCOORD3;
	float2 vTexCoords	: TEXCOORD4;
	float2 vTexCoords1	: TEXCOORD5;
	float4  Depth : TEXCOORD6;
};


struct DepthVStoPS {
	float4 vPos			: POSITION0;
	float3 vLight		: TEXCOORD0;
	float2 vTexCoords : TEXCOORD1;
};
struct ps_OU {
	float4 oCol  : COLOR0;
	float4 oCol1 : COLOR1;
	float4 oCol2 : COLOR1;
	//float4 oCol3 : COLOR1;
};
//###############  Input VS  ###############
struct visPP 
{
   float4 inPos : POSITION;
   float2 inTex	: TEXCOORD0;
};

//###############  Output VS  ##############
struct pisPP 
{
   float4 Pos : POSITION;
   float2 Tex : TEXCOORD0;
};
//##################  VS  ##################
void vs_PW_TBN_TC( in sv_TBN_TC IN, out sp_PW_TBN_TC OUT ) {
	OUT.vPos		= mul(IN.vPos,g_mWorldViewProj);
	OUT.pWorld		= mul(IN.vPos,g_mWorldViewProj);
	OUT.vNormal     = normalize(mul(IN.vNormal,g_mWorld));
	OUT.vTangent    = normalize(mul(IN.vTangent, g_mWorld));
	OUT.vBinormal   = normalize(mul(IN.vBinormal, g_mWorld));
	OUT.vTexCoords	= IN.vTexCoords;
	OUT.vTexCoords1	= IN.vTexCoords1;
	//OUT.Depth     == mul(IN.vPos,g_mWorld);
	OUT.Depth     =  mul(IN.vPos, g_mWorld);
}



inline float4 EncodeDepthNormal( float fDepth, float3 vNormal )
{
	float4 cEncripted;
	cEncripted.xyz = vNormal.xyz;
	cEncripted.w   = fDepth;
	return cEncripted;
}

inline float Luminance( float3 c )
{
	return dot( c, float3(1.0, 1.0, 1.0) );
}
float4 diffuseColor;

void DepthVS( float4 inPosition : POSITION, float2 texcoord : TEXCOORD0,out DepthVStoPS OUT)
{
	float4 vPosW = mul( inPosition,g_mWorld );
	OUT.vPos = mul( inPosition,g_mWorldViewProj );
	OUT.vLight = vLightPos.xyz - vPosW.xyz;
	OUT.vTexCoords = texcoord;
	//return OUT;
}
float4 DepthPS( in DepthVStoPS IN): COLOR0
{
	if (tex2D(sDiffuse,IN.vTexCoords).a < 0.5f) discard;
	return float4(length(IN.vLight.xyz),0,0,0);
}
   float DOF_NearFocus = 50.0f;
   float DOF_FarFocus = 500.0f;


	#define MAX_SAMPLES int(40)		//number of maximum samples
	#define MIN_SAMPLES int(5)		//number of minimum samples
	#define PARALLAX_OFFSET float(0.05)	//height of extruded surface
	#define FINAL_INTERSECTION_LOOPS int(5)	//Quantity of cycles of accuracy


//Only for testing.
//These options for algorithm of increase in contrast parallax maps
		#define CONTRAST_COEF_Q1 float(0.6)
		#define CONTRAST_COEF_Q2 float(1)

ps_OU NormalMapPS( in sp_PW_TBN_TC IN): COLOR0
 {
	ps_OU OUT;
	
    	float Pdepth = tex2D(sDepth,  IN.vTexCoords).r;
	 float fLength = length(camposG-IN.Depth.xyz);
	 
	 
	 if( EnableParalax >0) 
	 if( fLength < 200.0f ) 
	if( Pdepth > 0.0f )
	{
	
	float3	PosCam=normalize(camposG-IN.Depth);
	float3x3 matTangentSpace=float3x3(IN.vTangent,IN.vBinormal,IN.vNormal);
	matTangentSpace = transpose(matTangentSpace);
	float3 CamPosPlx = normalize(mul(PosCam,matTangentSpace));

	const int maxSamples = MAX_SAMPLES;
  	const int minSamples = MIN_SAMPLES;
	float fParallaxOffset = PARALLAX_OFFSET;
 	int nNumSteps = lerp( maxSamples, minSamples, CamPosPlx.z );
  	float2 vDelta = -CamPosPlx.xy * fParallaxOffset;

   	float fStepSize = 1.0 / nNumSteps;
   	float2 vTexOffsetPerStep = fStepSize * vDelta;
   	double2 vTexCurrentOffset = IN.vTexCoords;
   	float fCurrHeight = 0;
   	float  fCurrentBound = 1.0;

   	float Q1 = CONTRAST_COEF_Q1;
   	float Q2 = CONTRAST_COEF_Q2;

  	for(;fCurrHeight < fCurrentBound;fCurrentBound -= fStepSize)
   	{
   		vTexCurrentOffset.xy += vTexOffsetPerStep;

    	fCurrHeight = tex2Dlod(sDepth, float4(vTexCurrentOffset.xy,0,0) ).r;
	}

    float4 offsetBest = float4(vTexCurrentOffset,0,0);
    vTexCurrentOffset.xy -= vTexOffsetPerStep;

  	float fPrevHeight = tex2Dlod( sDepth, float4(vTexCurrentOffset.xy,0,0)  ).r;

    float error = 1.0;
    float t1 = fCurrentBound ;
    float t0 = t1 + fStepSize;
    float delta1 = t1 - fCurrHeight;
    float delta0 = t0 - fPrevHeight;
    float4 intersect = float4(vDelta, vDelta + IN.vTexCoords);

    for (int i=0; i<FINAL_INTERSECTION_LOOPS && abs(error) > 0.01; i++)
    { 
      float denom = (delta1 - delta0);
      float t = (t0 * delta1 - t1 * delta0) / denom;
      offsetBest.xy = -t * intersect.xy + intersect.zw;
  
  	  float NB = tex2Dlod(sDepth, offsetBest).r;

      error = t - NB;
      if (error < 0)
      {
         delta1 = error;
         t1 = t;
      }
      else
      {
         delta0 = error;
         t0 = t;
      }
    }
    IN.vTexCoords=offsetBest.xy;
	}
	
	//float3 vNormal = 2*tex2D(sNormals, IN.vTexCoords)-1;
	float3 vNormal = normalize(tex2D(sNormals, IN.vTexCoords) * 2.0f - 1.0f);

	float3x3 mTangentToWorld = transpose( float3x3( IN.vTangent, IN.vBinormal, IN.vNormal ) );
	

	float3   vNormalWorld    = mul( mTangentToWorld, vNormal );
	float4	cD = tex2D(sDiffuse,IN.vTexCoords);
	clip(cD.a - 0.5f); // mask
	//if (cD.a < 0.5f) discard;
	OUT.oCol = float4(cD.rgb,Luminance( tex2D(sSpecular, IN.vTexCoords) ));
	OUT.oCol1    = EncodeDepthNormal(IN.pWorld.z/IN.pWorld.w,vNormalWorld);

	float CloseDepth=saturate(1-(IN.pWorld.z/DOF_NearFocus));	
    float FarDepth=CloseDepth+saturate((-DOF_NearFocus+IN.pWorld.z)/DOF_FarFocus);	
    float depth = IN.pWorld.z/IN.pWorld.w;

	float far=500;
	float near=1;
    OUT.oCol2.x     = Luminance( tex2D(sBright, IN.vTexCoords) );
    OUT.oCol2.y    = 2.0f * near * far / (far + near - (2.0f * depth - 1.0f) * (far - near));  ;
    OUT.oCol2.z   =depth;
    OUT.oCol2.w		= 0 ;

	return OUT;
}


technique NormalMap {
	pass p0 {
	    AlphaBlendEnable = false;
		vertexshader	= compile vs_3_0 vs_PW_TBN_TC();
		pixelshader		= compile ps_3_0 NormalMapPS();
	}
}
technique Depth {
	pass p0 {
		vertexshader	= compile vs_3_0 DepthVS();
		pixelshader		= compile ps_3_0 DepthPS();
		AlphaBlendEnable = false;
		CullMode = CCW;
	}
}
