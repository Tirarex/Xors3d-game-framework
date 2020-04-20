
//##################  Varriables  ##################
const float4x4 g_mWorldViewProj	: MATRIX_WORLDVIEWPROJ;
const float4x4 g_mWorld			: MATRIX_WORLD;
const float4x4 g_mView 			: MATRIX_VIEWINVERSE;
const float4x4 g_mViewProjInv	: MATRIX_VIEWPROJINVERSE;
const float4x4 g_mViewProj		: MATRIX_VIEWPROJ;


float3 	vCamPos  : CAMERA_POSITION;;

const float SSAO_POWER   = 1.1;
const float2 TextureSize = float2(1920, 1080);
const int BLUR_RADIUS    = 4;

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



static const float radius = 4.0f; // ������ ����� ���������.
static const float minCrease = 0.6f; // ����������� ��� ����������� ��������� ������������� ������ ������������.
static const float creaseScale = 2.0f; // ���� ���������.
static const float distScale = 100.0f; // ����������� ���������� ������� ��������� � ����������� �� �����������.
static const float maxDist = 200.0f; // ������������ ��������� ����������� ���������.
static const float minDist = 2.0f;
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



// parameters (you'd probably want to use them as uniforms to more easily tweak the effect)
int kernelSize = 8;


// tile noise texture over screen based on screen dimensions divided by noise size
 float2 noiseScale = float2(800.0f/4.0f, 600.0f/4.0f); 



float4 extractViewPos( float2 TexCords, float fDepth ) {

	float4 vWorldPos;
	
	vWorldPos.x = TexCords.x * 2.0f - 1.0f;
	vWorldPos.y = -(TexCords.y * 2.0f - 1.0f);
	vWorldPos.z = fDepth;
	vWorldPos.w = 1.0f;
	vWorldPos = mul(vWorldPos, g_mViewProjInv);
    vWorldPos /= vWorldPos.w;
	return vWorldPos;	
}

float3 convertCameraSpaceToScreenSpace(float3 cameraSpace) {
    float4 clipSpace = mul(g_mViewProjInv, float4(cameraSpace, 1.0));
    float3 NDCSpace = clipSpace.xyz / clipSpace.w;
    float3 screenSpace = 0.5 * NDCSpace + 0.5;
    return screenSpace;
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

float GetDepth(float2 nuv)
{
     return  tex2D(sGBNormals,nuv.xy).w;
}
float sampleRadius=0.1;
float distanceScale=800;
float4x4 Projection;

#define SSAO_Side 6.0								//Set number of sides for ambient occlusion. Larger value means smoother edges. [4.0 6.0 8.0 10.0 12.0]
		#define SSAO_Depth 6.0								//Set number of layers for ambient occlusion. Larger value means smoother darkness. [3.0 6.0 9.0 12.0]
		#define SSAO_Size 40								//Set ambient occlusion size. [10 20 30 40 50 60]
		#define SSAO_Strength 1.0							//Set darkness level of ambient occlusion. [0.5 1.0 1.5 2.0 2.5 3.0]
		const float ssaorad = 1;
		const float ssaonoise = 1.0;
	
	
 float near=0;
 float far=100;
	
float ld(float depth) {
    return (2.0 * near) / (far + near - depth * (far - near));
}
float4 EffectProcess( float2 Tex : TEXCOORD0 ) : COLOR0

 {

	
	
	float ao = 1.0;

		float pi = 3.1415927;
		
		
	float pixeldepth = tex2D(sGBNormals, Tex).a;
		
		float3 norm = normalize(tex2D(sGBNormals, Tex).rgb);
		float3 projpos = GetPosition(Tex.xy,pixeldepth); 
	//	vec2 noiseAO = vec2(getnoise(texcoord.xy),getnoise(vec2(texcoord.x,-texcoord.y)))*2-1;
		float2 noiseAO = tex2D( rand, Tex * 200.0 ).rgb;
		
		float rprogress = 0.0;
		float sprogress = 1.0;
	
	
	float pw = 1.0/ 1366;
	float ph = 1.0/ 768;
	float aspectRatio=(16/9);
		
	
		float aosize = SSAO_Size*pw*768/1280;
		float projrad = clamp(distance(convertCameraSpaceToScreenSpace(projpos + float3(ssaorad,ssaorad,ssaorad)).xy,Tex.xy),aosize/2,aosize);
		noiseAO = noiseAO*projrad/(SSAO_Depth*sqrt(SSAO_Side))*768/1280*ssaonoise;
		
			for (int i = 0; i < SSAO_Depth; i++) {
				for (int j = 0; j < SSAO_Side; j++) {
					float2 samplecoord = float2(cos(rprogress*pi/180),sin(rprogress*pi/180))*(sprogress*projrad*float2(1,aspectRatio)) + Tex.xy + noiseAO;
					float sample = tex2D(sGBNormals,samplecoord).w;
					float3 sprojpos = GetPosition(samplecoord,sample);
					float angle = pow(min(1.0-dot(norm,normalize(sprojpos-projpos)),1.0),2.0);
					float dist = pow(min(abs(ld(sample)-ld(pixeldepth)),0.015)/0.015,2.0);
					float temp = min(dist+angle,1.0);
					ao += pow(temp,3.0);
					rprogress += 360/SSAO_Side;
				}
				sprogress = (i+1)/SSAO_Depth;
				rprogress += 90/SSAO_Side;
			}

			ao /= SSAO_Side*SSAO_Depth;
			ao = pow(ao,SSAO_Strength);
	
	
	return float4(ao,ao,ao,1);
	
	
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

// float4 cDiffuse = (TexelBlur.y != 0.0) ? tex2D(diff, IN.TexCoords.xy) : float4(1.0, 1.0, 1.0, 1.0);

 float4 cDiffuse = tex2D(diff, IN.TexCoords.xy);
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
