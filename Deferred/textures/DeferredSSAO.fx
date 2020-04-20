 float4x4 g_mViewProjInv	: MATRIX_VIEWPROJINVERSE;

//#define HISAMPLE

const float SSAO_POWER   = 2.8;
const float2 TextureSize = float2(800.0, 600.0);
const int BLUR_RADIUS    = 4;
const half fScale = 0.0005;///0.025f; 
float FarPlane  = 0.1;
float2 TexelBlur;

texture Diffuse;
texture Random;
texture Depth;
texture Screen;

const texture  tGBNormals;
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

sampler2D diff = sampler_state {
    Texture = (Diffuse);
    MinFilter = LINEAR;
    MagFilter = LINEAR;
    MipFilter = LINEAR;
};

sampler2D rand = sampler_state {
    Texture = (Random);
    MinFilter = POINT;
    MagFilter = POINT;
    MipFilter = POINT;
};


sampler depthtex = sampler_state {
  Texture = (Depth);
AddressU 		= Clamp;
AddressV 		= Clamp;
MinFilter 	= Linear;
MagFilter 	= Linear;
MipFilter 	= Linear;
};

sampler2D screentex = sampler_state {
    Texture   = <Screen>;
    MinFilter = LINEAR;
    MagFilter = LINEAR;
    MipFilter = LINEAR;
    ADDRESSU  = CLAMP;
    ADDRESSV  = CLAMP;
    ADDRESSW  = CLAMP;
};


struct vi 
{
 float4 Position  : POSITION0;
 float2 TexCoords : TEXCOORD0;
};

struct pi
{
 float4 Position  : POSITION0;
 float2 TexCoords : TEXCOORD1;
};

float4 EffectProcess( float2 Tex : TEXCOORD0 ) : COLOR0
{
  half n = 0;

  const half fComp  = 0.01443375673;

  const half step   = 1.0;

  const half3 arrKernel[8] =
  {
     half3( fComp, fComp, fComp)*(n+=step),
     half3(-fComp,-fComp,-fComp)*(n+=step),
     half3(-fComp,-fComp, fComp)*(n+=step),
     half3(-fComp, fComp,-fComp)*(n+=step),
     half3(-fComp, fComp ,fComp)*(n+=step),
     half3( fComp,-fComp,-fComp)*(n+=step),
     half3( fComp,-fComp, fComp)*(n+=step),
     half3( fComp, fComp,-fComp)*(n+=step),
  };

 half3 rotSample = 2.0 * tex2D(rand, Tex * TextureSize / 4.0 ).rgb - 1.0;
 half fSceneDepth = tex2D( sGBNormals, Tex ).a;       
 half fSceneDepthScaled = fSceneDepth * FarPlane;  

 half fScaleXY = 2.0 * (1.0 + 25.0 * fSceneDepth / FarPlane)*fScale;
 half fScaleZ  = 5.0*fScale;
 half3 vSampleScale = half3(fScaleXY, fScaleXY, fScaleZ) * saturate(fSceneDepthScaled / 5.3f) * (1.f + fSceneDepthScaled / 8.f ); 

 float fDepthRangeScale = 0.85 * FarPlane / vSampleScale.z;
  
 vSampleScale.xy *= 1.0f / fSceneDepthScaled;
 vSampleScale.z  *= 2.0f / FarPlane;

 float fDepthTestSoftness = 96.0 / vSampleScale.z;

 half4 vFactor  = 0.0;
 half  fHQScale = 0.5f;
 half  fDefVal  = 0.6f;

 half4 arrSceneDepth2[2];      
 half3 vIrrSample;
 half4 vDistance;
 float4 fRangeIsInvalid;

   half3 giSum;  
   half3 giCol;
 for (int i=0; i < 2; i++)
  {    
  
  
   vIrrSample = reflect(arrKernel[i*4+0], rotSample) * vSampleScale;      
   arrSceneDepth2[0].x = tex2D( sGBNormals, Tex + vIrrSample.xy ).a + vIrrSample.z;  
#ifdef HISAMPLE
   vIrrSample.xyz *= fHQScale;
   arrSceneDepth2[1].x = tex2D( sGBNormals, Tex + vIrrSample.xy ).a + vIrrSample.z;  
#endif

   vIrrSample = reflect(arrKernel[i*4+1], rotSample) * vSampleScale;      
   arrSceneDepth2[0].y = tex2D( sGBNormals, Tex + vIrrSample.xy ).a + vIrrSample.z;  
#ifdef HISAMPLE
   vIrrSample.xyz *= fHQScale;
   arrSceneDepth2[1].y = tex2D( sGBNormals, Tex + vIrrSample.xy ).a + vIrrSample.z;  
#endif

   vIrrSample = reflect(arrKernel[i*4+2], rotSample) * vSampleScale;      
   arrSceneDepth2[0].z = tex2D( sGBNormals, Tex + vIrrSample.xy ).a + vIrrSample.z;  
#ifdef HISAMPLE
   vIrrSample.xyz *= fHQScale;
   arrSceneDepth2[1].z = tex2D( sGBNormals, Tex + vIrrSample.xy ).a + vIrrSample.z;  
#endif

   vIrrSample = reflect(arrKernel[i*4+3], rotSample) * vSampleScale;      
   arrSceneDepth2[0].w = tex2D( sGBNormals, Tex + vIrrSample.xy ).a + vIrrSample.z;  
#ifdef HISAMPLE
   vIrrSample.xyz *= fHQScale;
   arrSceneDepth2[1].w = tex2D( sGBNormals, Tex + vIrrSample.xy ).a + vIrrSample.z;  
#endif

   vDistance              = fSceneDepth - arrSceneDepth2[0]; 
   float4 vDistanceScaled = vDistance * fDepthRangeScale;
   fRangeIsInvalid        = 0.5 * ( saturate( abs(vDistanceScaled) ) + saturate( vDistanceScaled ) );  
   vFactor               += lerp( saturate(-vDistance * fDepthTestSoftness), fDefVal, fRangeIsInvalid);

#ifdef HISAMPLE
   vDistance              = fSceneDepth - arrSceneDepth2[1]; 
   vDistanceScaled        = vDistance * fDepthRangeScale;
   fRangeIsInvalid        = 0.5 * ( saturate( abs(vDistanceScaled) ) + saturate( vDistanceScaled ) );  
   vFactor               += lerp( saturate(-vDistance * fDepthTestSoftness), fDefVal, fRangeIsInvalid);
#endif
  }

#ifdef HISAMPLE
 float fAO = saturate( dot( vFactor, 1.0/8.0) );
#else
 float fAO = saturate( dot( vFactor, 1.0/4.0) );
#endif

 return fAO;
}

static const float radius = 4.0f; // ������ ����� ���������.
static const float minCrease = 0.0f; // ����������� ��� ����������� ��������� ������������� ������ ������������.
static const float creaseScale = 1.0f; // ���� ���������.
static const float distScale = 10.0f; // ����������� ���������� ������� ��������� � ����������� �� �����������.
static const float maxDist = 2000.0f; // ������������ ��������� ����������� ���������.
static const float minDist = 20.0f;
static const float2 rndTable[ 12 ] = { { -0.326212f, -0.405805f },
									   { -0.840144f, -0.073580f },
									   { -0.695914f,  0.457137f },
									   { -0.203345f,  0.620716f },
									   {  0.962340f, -0.194983f },
									   {  0.473434f, -0.480026f },
									   {  0.519456f,  0.767022f },
									   {  0.185461f, -0.893124f },
									   {  0.507431f,  0.064425f },
									   {  0.896420f,  0.412458f },
									   { -0.321940f, -0.932615f },
									   { -0.791559f, -0.597705f } }; // ������� � ������������ �������� �����.




float4 extractViewPos( float2 texCoord ) {
	float4 vPos = { texCoord.x * 2.0f - 1.0f,-( texCoord.y * 2.0f - 1.0f ),tex2D( sGBNormals, texCoord ).w,1.0f };
	vPos = mul( vPos, g_mViewProjInv );
	vPos /= vPos.w;
	return vPos;	
}


float4 EffectProcess2( float2 Tex : TEXCOORD0 ) : COLOR0

 {
	 float3 vPos = extractViewPos(  Tex ).xyz;
	//if ( vPos.z > maxDist ) {
	//	return float4( 1.0f, 1.0f, 1.0f, 1.0f );
	//}
	 float3 vNor =   tex2D( sGBNormals, Tex ).xyz;
	
	// ������ ���������� �������� (������������ wrap ��������) ��� ������� ������� � ���������� ��������.
	 float2 rotTexCoord = { Tex.x * TextureSize.x / 4.0f, Tex.y * TextureSize.y / 4.0f };
	
	// ������������� ����������� �������.
	 float3 plane = tex2D( sGBNormals, rotTexCoord ).xyz;
	
	// ���������� ������������ ���� ��������� ������� �������.
	float att = 0.0f;
	
	// ������ 12 �������� - �� ������ ��� ������� ����.
	for ( int i = 0; i < 12; ++i ) {
		// ���������� � ����� ����� ����� ������� ���.
		float2 sampleTex;
		if ( vPos.z < minDist ) {
			sampleTex = Tex + ( radius * reflect( float3( rndTable[ i ], 0.0f ), plane ).xy ) / ( minDist * 2.0f );
		} else {
			sampleTex = Tex + ( radius * reflect( float3( rndTable[ i ], 0.0f ), plane ).xy ) / ( vPos.z * 2.0f );
		}
		
		// ���������������� view space ���������� �����, � ������� ������� ���.
		 float3 vRay = extractViewPos(  sampleTex ).xyz;
		
		// ������ ����.
		float3 vRayVec = vRay - vPos;
		
		// ���������� ����� ���� � ����������� ���.
		 float dist = length( vRayVec );
		vRayVec /= dist;
		
		// ������� ������� ���� ����� �������� � �����.
		// 1.0f - ������������ ���������, 0.0f - ����������� ���������.
		// minCrease - ����� ��������� ��������� ��������, ���� ���� ������� ����� 0.0f.
		// creaseScale - ��������� ���� ���������.
		 float normAtten = saturate( dot( vRayVec, vNor ) * creaseScale - minCrease );
		
		// ������� ���������� ��������� � ����������� �� ���������� �� ����������� (����� ����).
		 float rangleAtten = saturate( ( distScale - dist ) / distScale );
		
		// �������� ��� ������������ � ������������ � ����� ����������.
		att += normAtten * rangleAtten; //
		//att +=normAtten;
	}
	//const float res = saturate( pow( ( 1.0f - att / 12.0f ) * 1.0f, 1.0f ) );
	 float result = 1.0f - att / 12.0f;
	 
	 if ( vPos.z > maxDist ) {
		//result = 1.0f ;
	}
	 
	return float4( result, result, result, 1.0f );
}




float4 psBlurSSAO( in pi IN ) : COLOR 
{
 float2 vOffset  = 0.9 / TextureSize;

 float fAOScale     = 1.0 + BLUR_RADIUS;
 float fAO          = tex2D(screentex, IN.TexCoords.xy).x * fAOScale;

// return fAO / fAOScale;

 float fCenterDepth = tex2D(depthtex,  IN.TexCoords.xy).x;

 for (int i = 0; i < BLUR_RADIUS; i++)
 {
  float fGauss = BLUR_RADIUS - i;
  float2 vNextSample = IN.TexCoords.xy + (i+1) * vOffset * TexelBlur;
  float2 vPrevSample = IN.TexCoords.xy - (i+1) * vOffset * TexelBlur;
  float fNextDepth = tex2D(sGBNormals, vNextSample).w;
  float fPrevDepth = tex2D(sGBNormals, vPrevSample).w;
  fNextDepth -= fCenterDepth;
  fPrevDepth -= fCenterDepth;
  float fNextWeight = saturate( 1.0 - 0.1 * pow(fNextDepth, 4.0) );
  float fPrevWeight = saturate( 1.0 - 0.1 * pow(fPrevDepth, 4.0) );
  fAO += tex2D(screentex, vNextSample).x * fGauss * fNextWeight;
  fAO += tex2D(screentex, vPrevSample).x * fGauss * fPrevWeight;
  fAOScale += fGauss * (fNextWeight + fPrevWeight);
 }


 fAO = pow( saturate(fAO / fAOScale), SSAO_POWER);

 float4 cDiffuse = (TexelBlur.y != 0.0) ? tex2D(diff, IN.TexCoords.xy) : float4(1.0, 1.0, 1.0, 1.0);

 return fAO * cDiffuse;
}

technique SSAO
{
 pass p0
 {
  AlphaBlendEnable = 0;
  VertexShader = null;
  PixelShader = compile ps_3_0 EffectProcess();
 }
}

technique BlurSSAO
{
 pass p0 
 {
  AlphaBlendEnable = 0;
  ZWriteEnable     = 1;
  vertexshader	   =  null; 
  pixelshader      = compile ps_3_0 psBlurSSAO();
 }
}
