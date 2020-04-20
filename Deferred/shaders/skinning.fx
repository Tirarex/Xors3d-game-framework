
float4x4 bonesMatrixArray[60];

const float4x4 g_mWorldViewProj	: MATRIX_WORLDVIEWPROJ;
const float4x4 g_mWorld			: MATRIX_WORLD;
const float4x4 g_mView 			: MATRIX_VIEWINVERSE;
const float4x4 g_mViewProjInv	: MATRIX_VIEWPROJINVERSE;
float4x4 matViewProj : MATRIX_VIEWPROJ;
texture diffuseTexture : TEXTURE_0;
sampler diffuseSampler = sampler_state
{
    Texture = <diffuseTexture>;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};


struct sv_TBN_TC {
	float4 vPos			: POSITION0;
	float3 vNormal    	: NORMAL;
	float3 vTangent   	: TANGENT;
	float3 vBinormal  	: BINORMAL;
	float2 vTexCoords	: TEXCOORD0;
	float2 vTexCoords1	: TEXCOORD1;
	float4 BlendWeights : BLENDWEIGHT;
    float4 BlendIndices : BLENDINDICES;
};

struct sp_PW_TBN_TC {
	float4 vPos			: TEXCOORD0;
	float4 pWorld		: POSITION0;
	float3 vNormal      : TEXCOORD1;
	float3 vTangent   	: TEXCOORD2;
	float3 vBinormal  	: TEXCOORD3;
	float2 vTexCoords	: TEXCOORD4;
};

struct ps_OU {
	float4 oCol  : COLOR0;
	float4 oCol1 : COLOR1;
};


float3 AutoNormalGen(sampler2D sample,float2 texCoord) {
   float off = 1.0 / 512;
   float4 lightness = float4(0.2,0.59,0.11,0);
   // Take all neighbor samples
   float4 s00 = tex2D(sample, texCoord + float2(-off, -off));
   float4 s01 = tex2D(sample, texCoord + float2( 0,   -off));
   float4 s02 = tex2D(sample, texCoord + float2( off, -off));

   float4 s10 = tex2D(sample, texCoord + float2(-off,  0));
   float4 s12 = tex2D(sample, texCoord + float2( off,  0));

   float4 s20 = tex2D(sample, texCoord + float2(-off,  off));
   float4 s21 = tex2D(sample, texCoord + float2( 0,    off));
   float4 s22 = tex2D(sample, texCoord + float2( off,  off));

   // Slope in X direction
   float4 sobelX = s00 + 2 * s10 + s20 - s02 - 2 * s12 - s22;
   // Slope in Y direction
   float4 sobelY = s00 + 2 * s01 + s02 - s20 - 2 * s21 - s22;

   // Weight the slope in all channels, we use grayscale as height
   float sx = dot(sobelX, lightness);
   float sy = dot(sobelY, lightness);

   // Compose the normal
   float3 normal = normalize(float3(sx, sy, 1));

   // Pack [-1, 1] into [0, 1]
   return normal * 0.5 + 0.5;
}
void vs_PW_TBN_TC( in sv_TBN_TC IN, out sp_PW_TBN_TC OUT)
{

    
    // extract bones indicies and weights from vertex
    int    IndexArray[4]        = (int[4])D3DCOLORtoUBYTE4(IN.BlendIndices);
    float  BlendWeightsArray[4] = (float[4])IN.BlendWeights;
    // variable for computing last bone weight
    float LastWeight            = 0.0f;
    // cycle around bones
    OUT.vNormal               = float3(0.0f, 0.0f, 0.0f);
    OUT.vTangent               = float3(0.0f, 0.0f, 0.0f);
    OUT.vBinormal               = float3(0.0f, 0.0f, 0.0f);
    OUT.vPos             = float4(0.0f, 0.0f, 0.0f, 0.0f);
 //   IN.vPos		= mul(IN.vPos,matViewProj);
	//IN.pWorld		= mul(IN.vPos,g_mWorldViewProj);
    
     for(int i = 0; i < 3; i++)
    {
        LastWeight  = LastWeight + BlendWeightsArray[i];
        OUT.vPos += mul(IN.vPos, bonesMatrixArray[IndexArray[i]]) * BlendWeightsArray[i];
        OUT.vNormal   += mul(IN.vNormal,   bonesMatrixArray[IndexArray[i]]) * BlendWeightsArray[i];
        OUT.vTangent  += mul(IN.vTangent,   bonesMatrixArray[IndexArray[i]]) * BlendWeightsArray[i];
        OUT.vBinormal  += mul(IN.vBinormal,   bonesMatrixArray[IndexArray[i]]) * BlendWeightsArray[i];   
    }
    LastWeight  = 1.0f - LastWeight; 
    
    
    OUT.vPos += mul(IN.vPos, bonesMatrixArray[IndexArray[3]]) * LastWeight;
 
    OUT.vNormal   += mul(IN.vNormal, bonesMatrixArray[IndexArray[3]]) * LastWeight;
    OUT.vTangent   += mul(IN.vTangent, bonesMatrixArray[IndexArray[3]]) * LastWeight;
    OUT.vBinormal   += mul(IN.vBinormal, bonesMatrixArray[IndexArray[3]]) * LastWeight;

	OUT.pWorld		= mul(OUT.vPos,matViewProj);
    OUT.vPos  = mul(OUT.vPos, matViewProj);
    OUT.vNormal    = normalize(OUT.vNormal);
 	OUT.vTangent    = normalize(OUT.vTangent);
 	OUT.vBinormal    = normalize(OUT.vBinormal);
    OUT.vTexCoords = IN.vTexCoords;
}



ps_OU NormalMapPS( in sp_PW_TBN_TC IN): COLOR0
 {
	ps_OU OUT;
	
float4	cD = tex2D(diffuseSampler,IN.vTexCoords);
		float3 vNormal = (AutoNormalGen(diffuseSampler, IN.vTexCoords.xy).xyz - (0.5).xxx);

	float3x3 mTangentToWorld = transpose( float3x3( IN.vTangent, IN.vBinormal, IN.vNormal ) );
	

	float3   vNormalWorld    = mul( mTangentToWorld, float3(0.51,0.47,1) );
	
	//clip(cD.a - 0.5f); // mask
	if (cD.a < 0.5f) discard;
	OUT.oCol = float4(cD.rgb,0);
	OUT.oCol1    = float4(vNormalWorld,IN.vPos.z/IN.vPos.w);


	return OUT;
}

technique Skinned
{
    pass p0
    {
        VertexShader = compile vs_3_0 vs_PW_TBN_TC();
        PixelShader  = compile ps_3_0 NormalMapPS();
    }
}
