//##################  Varriables  ##################
const float4x4 MatWorldViewProj;

float NoiseStrength = 0.1f;
float RandomValue = 0.0f;
float  time	  : CURRENT_TIME;
const float sila =0.1;

const texture tDiffuse;
sampler TexDiffuse=sampler_state {
    Texture   = <tDiffuse>;
    AddressU = Clamp;
    AddressV = Clamp;
    MinFilter = Point;
    MagFilter = Linear;
    MipFilter = Linear;
};


texture noiseTexture;
sampler noiseSample = sampler_state
{
	Texture   = <noiseTexture>;
	AddressU  = Wrap;
	AddressV  = Wrap;
	MinFilter = Linear;
	MagFilter = Linear;
	MipFilter = Linear;
};

const texture tEmissive;
sampler TexEmissive=sampler_state {
    Texture   = <tEmissive>;
    ADDRESSU  = WRAP;
    ADDRESSV  = WRAP;
    ADDRESSW  = WRAP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};

texture tBright ;
sampler sBright = sampler_state
{
	Texture	  = <tBright>;
	ADDRESSU  = CLAMP;
    ADDRESSV  = CLAMP;
    ADDRESSW  = CLAMP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};

texture LensTexture;
sampler Lens 	= sampler_state {
		Texture   	= <LensTexture>;
      ADDRESSU  = WRAP;
    ADDRESSV  = WRAP;
    ADDRESSW  = WRAP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};


const texture DiTex;
sampler distortionSample = sampler_state
{
	// Assing texture to sampler
	Texture   = <DiTex>;
	// We always need clamping for textures
	AddressU  = Clamp;
	AddressV  = Clamp;
	// Linear texture filtering, also you may use "Point" or "None"
	MinFilter = Linear;
	MagFilter = Linear;
	MipFilter = Linear;
};

struct vis {
	float4 Position : POSITION0;
	float2 TexCoords : TEXCOORD0;
};
struct vid {
	float4 Position : POSITION0;
	float2 TexCoords : TEXCOORD0;
};
//###############  Output VS  ##############
struct pis {
	float4 Position : POSITION0;
	float2 TexCoords : TEXCOORD0;
};
struct pid {
	float4 Position : POSITION0;
	float Depth : TEXCOORD0;
	float3 ProjTex : TEXCOORD1;
};


//##################  VS  ##################
void vs( in vis IN, out pis OUT ) {
	OUT.Position		= mul(IN.Position,MatWorldViewProj);
	OUT.TexCoords		= IN.TexCoords;
}
void vsd( in vid IN, out pid OUT ) {
	OUT.Position		= mul(IN.Position,MatWorldViewProj);
	OUT.Depth			= OUT.Position.z;
	OUT.ProjTex.x		= 0.5f*(OUT.Position.w+OUT.Position.x);
	OUT.ProjTex.y		= 0.5f*(OUT.Position.w-OUT.Position.y);
	OUT.ProjTex.z		= OUT.Position.w;
}

static const int g_cKernelSize = 13;
static const float sw = 128;

float2 PixelKernel[g_cKernelSize] =
{
    { -6/sw , 0 },
    { -5/sw , 0 },
    { -4/sw , 0 },
    { -3/sw , 0 },
    { -2/sw , 0 },
    { -1/sw , 0 },
    {  0, 0 },
    {  1/sw , 0 },
    {  2/sw , 0 },
    {  3/sw , 0 },
    {  4/sw , 0 },
    {  5/sw , 0 },
    {  6/sw , 0 },
};

float2 PixelKernelV[g_cKernelSize] =
{
    { 0, -6/sw },
    { 0, -5/sw },
    { 0, -4/sw },
    { 0, -3/sw },
    { 0, -2/sw },
    { 0, -1/sw },
    {  0, 0 },
    { 0,  1/sw },
    { 0,  2/sw },
    { 0,  3/sw },
    { 0,  4/sw },
    { 0,  5/sw },
    { 0,  6/sw },
};

float2 TexelKernel[g_cKernelSize]
<
    string ConvertPixelsToTexels = "PixelKernel";
>;


static const float BlurWeights[g_cKernelSize] = 
{
    0.002216,
    0.008764,
    0.026995,
    0.064759,
    0.120985,
    0.176033,
    0.199471,
    0.176033,
    0.120985,
    0.064759,
    0.026995,
    0.008764,
    0.002216,
};


float4 psDiffuseH( in pis IN ) : COLOR {
    float4 Color = 0;

    for (int i = 0; i < g_cKernelSize; i++)
    {    
        Color += tex2D( TexDiffuse, IN.TexCoords + PixelKernel[i].xy ) * BlurWeights[i];
    }

    return Color  ;
}

float4 psDiffuseV( in pis IN ) : COLOR {
    float4 Color = 0;

    for (int i = 0; i < g_cKernelSize; i++)
    {    
        Color += tex2D( TexDiffuse, IN.TexCoords + PixelKernelV[i].xy ) * BlurWeights[i];
    }

    return Color  ;//* 1.5 + tex2D(TexEmissive, IN.TexCoords);
}


float4 Noise( float2 texCoords )
{
	return tex2D( noiseSample, texCoords + float2(-RandomValue, RandomValue) );
}

// Pixel shader for bloom
float4 PSBloom(float2 texCoords : TEXCOORD0) : COLOR
{
    float4 color = tex2D(TexEmissive, texCoords);
    return 1.5f * (color - 0.29);
}


float4 BluF(float2 vTexCoord, float2 texScale, float sigma)
{
   float woffset=0.005;
   float4 color		= tex2D(TexDiffuse,vTexCoord);
   color		+=tex2D(TexDiffuse,vTexCoord+float2(woffset,0))*.55;
   color		+=tex2D(TexDiffuse,vTexCoord+float2(-woffset,0))*.55;
   color		+=tex2D(TexDiffuse,vTexCoord+float2(0,woffset))*.55;
   color		+=tex2D(TexDiffuse,vTexCoord+float2(0,-woffset))*.55;
   color		+=tex2D(TexDiffuse,vTexCoord+float2(woffset,woffset))*.31;
   color		+=tex2D(TexDiffuse,vTexCoord+float2(woffset,-woffset))*.31;
   color		+=tex2D(TexDiffuse,vTexCoord+float2(-woffset,-woffset))*.31;
   color		+=tex2D(TexDiffuse,vTexCoord+float2(-woffset,woffset))*.31;
   color		= color/4.44;
  
   return float4 (color);
}

float2 VignetteCenter =(0.5,0.5);
float VignetteRadius =0.85;
float VignetteAmount = -0.2;

float Contrast =0.1;
float Thresholds =0.1;
float rcomp;
float gcomp;
float bcomp;
half contr;
half desat;
half intdes;
half onedes;


float4 psHDR( in pis IN ) : COLOR {
    half2 coords = IN.TexCoords;
	coords = (coords - 0.5) * 2.0;
	half coordDot = 1-dot (coords,coords);
	
    float2 offset = tex2D(distortionSample, IN.TexCoords);
	offset -=  0.5f;
    IN.TexCoords=IN.TexCoords+offset* 0.25f;
     
     
       
	float4 c = float4(0.0f, 0.0f, 0.0f, 0.0f);
		c.a = tex2D(TexEmissive, IN.TexCoords).a;
		c.r = tex2D(TexEmissive, lerp(IN.TexCoords + float2(0.0f, 0.01f) * sila,IN.TexCoords,saturate (coordDot))).r;
		c.g = tex2D(TexEmissive, lerp(IN.TexCoords,IN.TexCoords,saturate (coordDot)) ).g;
		c.b = tex2D(TexEmissive, lerp(IN.TexCoords + float2(0.01f, 0.0f) * sila,IN.TexCoords,saturate (coordDot)) ).b;	


 	float2 tc = IN.TexCoords - VignetteCenter;
    float v = length(tc) / VignetteRadius;
    c.rgb += pow(v, 4) * VignetteAmount;


    
    
    
   
   //float3 frameTexel =c+tex2D(Lens, IN.TexCoords)*(tex2D(TexDiffuse, IN.TexCoords));
   // float3 frameTexel =tex2D(TexDiffuse, IN.TexCoords);
   //frameTexel= ToneMap( frameTexel);
   //frameTexel += Flares;
  
  float4 dscene=c;
  
	dscene.r = dscene.r * rcomp;
	dscene.g = dscene.g * gcomp;
	dscene.b = dscene.b * bcomp;

	float Intensity = 0.33 * dscene.r + 0.33 * dscene.g + 0.33 * dscene.b;

	intdes = Intensity * desat;
	onedes = 1 - desat;

	dscene.r = intdes + dscene.r * onedes;
	dscene.g = intdes + dscene.g * onedes;
	dscene.b = intdes + dscene.b * onedes;

	dscene.rgb = ((dscene.rgb - 0.5f) * max(contr, 0)) + 0.5f;    

  return float4 (dscene.rgb, 1.0f);
}

technique DS {
	pass p0 {

		vertexshader	= compile vs_2_0 vs();
		pixelshader		= compile ps_3_0 PSBloom();
	}
}


technique DiffuseH {
	pass p0 {
		//AlphaBlendEnable= 1;
		vertexshader	= compile vs_2_0 vs();
		pixelshader		= compile ps_2_0 psDiffuseH();
	}
}
technique DiffuseV {
	pass p0 {
		//AlphaBlendEnable= 1;
		vertexshader	= compile vs_2_0 vs();
		pixelshader		= compile ps_2_0 psDiffuseV();
	}
}


technique HDR {
	pass p0 {

		vertexshader	= compile vs_2_0 vs();
		pixelshader		= compile ps_3_0 psHDR();
		AlphaBlendEnable=true;
	SrcBlend=SRCALPHA;
	DestBlend=INVSRCALPHA; 	
	}
}

