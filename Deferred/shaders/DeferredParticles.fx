float4x4 matWorld 					: MATRIX_WORLD;
float4x4 matWorldViewProj 	: MATRIX_WORLDVIEWPROJ;


int	textureFiltering    		: TEXTURE_FILTERING;
int	anisotropyLevel     		: ANISOTROPY_LEVEL;

//Global
float  time	  : CURRENT_TIME;
float2 TexScale = { 10.0f, 10.0f };	
float3 Res    ;
float3 EntityColor=(1,1,1);
float3 lightDir = float3(0.5f, 0.5f, 1.0f);
float4 CamPos : CAMERA_POSITION;


//Particles
float Alpha 		= 1;
float Softness 	= 2;
int OnOff 			= 1;

//Water
float2 WaterSpeed = { 0.04f, 0.05f };
float2 WaterOffset  ; 
float3 Refraction 	;
float3 WaterNormal  ;
float4 Color        ;
float4 Foam         ;

texture DepthTexture;
sampler DEPTH 	= sampler_state {
		Texture   	= <DepthTexture>;
    AddressU 		= Clamp;
    AddressV 		= Clamp;
    MinFilter 	= Linear;
    MagFilter 	= Linear;
    MipFilter 	= Linear;
};

texture texture_layer0 : TEXTURE_0;
sampler SKIN 			= sampler_state
{
 	Texture       = <texture_layer0>;
  	ADDRESSU      = WRAP;
  	ADDRESSV      = WRAP;
  	ADDRESSW      = WRAP;	
  	MAGFILTER     = LINEAR;
  	MINFILTER     = LINEAR;
  	MIPFILTER     = LINEAR;
};

texture sceneTexture ;
sampler sceneSample = sampler_state
{
	Texture   = <sceneTexture>;
	AddressU  = Clamp;
	AddressV  = Clamp;
	MinFilter = Linear;
	MagFilter = Linear;
	MipFilter = Linear;
};

const texture tNormalW;
sampler TexNormalW=sampler_state {
    Texture   = <tNormalW>;
    ADDRESSU  = WRAP;
    ADDRESSV  = WRAP;
    ADDRESSW  = WRAP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};
const texture tNormalN;
sampler TexNormalN=sampler_state {
    Texture   = <tNormalN>;
    ADDRESSU  = WRAP;
    ADDRESSV  = WRAP;
    ADDRESSW  = WRAP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};

texture envTexture;
samplerCUBE envSampler = sampler_state
{
    Texture = <envTexture>;
    MinFilter = Linear;
    MagFilter = Linear;
    MipFilter = Linear;
    AddressU  = clamp;
    AddressV  = clamp;
    AddressW  = clamp;
};


const texture FoamTexture;
sampler FoamT=sampler_state {
    Texture   = <FoamTexture>;
    ADDRESSU  = WRAP;
    ADDRESSV  = WRAP;
    ADDRESSW  = WRAP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = POINT;
};


const texture CloudsTex1;
sampler Clouds1Sample = sampler_state {
    Texture   = <CloudsTex1>;
    ADDRESSU  = WRAP;
    ADDRESSV  = WRAP;
    ADDRESSW  = WRAP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};

const texture CloudsTex2;
sampler Clouds2Sample = sampler_state {
    Texture   = <CloudsTex2>;
    ADDRESSU  = WRAP;
    ADDRESSV  = WRAP;
    ADDRESSW  = WRAP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};

const texture CloudsTex3;
sampler Clouds3Sample = sampler_state {
    Texture   = <CloudsTex3>;
    ADDRESSU  = WRAP;
    ADDRESSV  = WRAP;
    ADDRESSW  = WRAP;
    MAGFILTER = LINEAR;
    MINFILTER = LINEAR;
    MIPFILTER = LINEAR;
};

struct VS_INPUT
{
		float4 Pos 			: POSITION;
		float2 Tex 			: TEXCOORD0;
		float3 Depth  		: TEXCOORD1;  
		float4 ParDepth		: TEXCOORD2;
		float4 P			: TEXCOORD3;
		float4 Pso			: TEXCOORD4;
		float3 vNormal    	: NORMAL;
};

struct VS_OUTPUT
{
  	    float4 Pos 			: POSITION0;
    	float2 Tex 			: TEXCOORD0;
 		float3 Depth   		: TEXCOORD1;
 		float4 ParDepth		: TEXCOORD2;    
 		float4 P			: TEXCOORD3;
 		float4 Pso			: TEXCOORD4;
 		float3 vNormal    	: TEXCOORD5;
 		float3 viewVec 	: TEXCOORD6;
 		
};

void VS (	 in VS_INPUT IN, out VS_OUTPUT OUT )
{
		OUT.vNormal     = normalize(mul(IN.vNormal,matWorld));
		OUT.Pos 					= mul(IN.Pos, matWorldViewProj);
		OUT.Depth 				= mul(IN.Pos, matWorldViewProj).xyw;
		OUT.P				  		= mul(IN.Pos, matWorld).xyzw;
		OUT.Tex 					= IN.Tex;		
		OUT.ParDepth  		= OUT.Pos;
		OUT.Pso	= mul(IN.Pos, matWorld);
		OUT.viewVec=IN.Pos;
};

float CloudsPos;
float CloudDensity = 1.25;
float3 CloudColor = float3(1.0f,0.9f,0.9f);
float3 LightDir   : LIGHT0_DIRECTION;


inline float2 GetRealUV(  float3 Depth)
{
		Depth.xz 			= -Depth.xz;
		float2 texelSize	= float2(1.0 / Res.x, 1.0 / Res.y );
		float2 UV     = (0.5 * Depth.xy / Depth.z + 0.5) + ( texelSize / 2.0 );
		return UV;
}

inline float GetDiffAlpha( float3 DepthPart,float3 Pos ,float4 ParDepth)
{

	    float2 UV=GetRealUV(DepthPart);
		float SceneDepth	= tex2D(DEPTH, UV).w;

		float Dist 				= distance(Pos,CamPos);		
		float CurDepth		= ParDepth.z / ParDepth.w;
		float DepthDiff	;
		
		if ( OnOff == 1 ) {
		 DepthDiff		= ( SceneDepth - CurDepth ) * Dist * ( Softness * Dist );
		} else {
		 DepthDiff		= ( SceneDepth - CurDepth ) * Dist * ( 1000 * Dist );
		}


		return saturate( DepthDiff )* Alpha;
}

float4 psClouds( in VS_OUTPUT IN ) : COLOR 
{
	float DepthDiff=GetDiffAlpha( IN.Depth,IN.P ,IN.ParDepth);
			
	float2 CloudUV1 = (IN.Tex) * 2; 	// MASK
	float2 CloudUV2 = (IN.Tex) * 4;
	CloudsPos=time*0.1;
	float MaskCloud = tex2D( Clouds2Sample, ( CloudUV1 ) + CloudsPos) * CloudDensity;
	float Clouds = saturate( ( tex2D( Clouds1Sample, ( CloudUV2 ) + CloudsPos) - MaskCloud));
	float3 Normalmap = tex2D( Clouds3Sample, ( CloudUV2 ) + CloudsPos ) * 2 - MaskCloud; 
	float Light = 0.25f + saturate( ( 0.4f + ( 1 - Clouds ) * 0.6f ) * ( 0.4f + ( dot( -LightDir, Normalmap ) ) * 0.5f ) );
	float Hori 			= 0.0;
	float HoriFade 	= 3.0;		
	float Fade = lerp(0, Clouds, saturate((IN.viewVec.y - Hori) / (HoriFade - Hori)));	
	return float4( Light * CloudColor, Fade*DepthDiff);
}
 
float4 SoftParticlePS (	in VS_OUTPUT IN ) : COLOR 
{
	
		Color = tex2D(SKIN,IN.Tex );
		Color.xyz=Color.xyz*EntityColor;
        float2 RUV= GetRealUV(IN.Depth);
		float DepthDiff=GetDiffAlpha( IN.Depth,IN.P ,IN.ParDepth);	
		//Color.xyz=Color.xyz*tex2D(sceneSample, RUV  );
		Alpha=Alpha*Color.a;
		Color.a = ( DepthDiff * Alpha );
		return Color;
};


float4 SoftBrightParticlePS (	in VS_OUTPUT IN ) : COLOR 
{
     	float2 RUV= GetRealUV(IN.Depth);
		Color = tex2D(SKIN,IN.Tex );
			Color.xyz=Color.xyz*EntityColor;
	
		Color.xyz=Color.xyz+tex2D(sceneSample,   RUV );
		float DepthDiff=GetDiffAlpha( IN.Depth,IN.P ,IN.ParDepth);		
		Alpha=Alpha*Color.a;
		Color.a = ( DepthDiff * Alpha );
		return Color;
};

float4 OnlyRenderPS (	in VS_OUTPUT IN ) : COLOR 
{
     	float2 RUV= GetRealUV(IN.Depth);
		Color = tex2D(SKIN,IN.Tex );
			Color.xyz=Color.xyz*EntityColor;
		Color.xyz=Color.xyz+tex2D(sceneSample,   RUV );		
		Alpha=Alpha*Color.a;
		Color.a = Alpha ;
		return Color;
};

float4 DecalPS (	in VS_OUTPUT IN ) : COLOR 
{
        float2 RUV= GetRealUV(IN.Depth);
		Color = tex2D(SKIN,IN.Tex )*4;
	
		Color.xyz=tex2D(sceneSample,   RUV )*Color.xyz;
		float DepthDiff=GetDiffAlpha( IN.Depth,IN.P ,IN.ParDepth);		
		Alpha=Alpha*Color.a;
		Color.a = ( DepthDiff * Alpha );
		return Color;
};

float4 RefractionPS (	in VS_OUTPUT IN ) : COLOR 
{
     	float2 RUV= GetRealUV(IN.Depth);
		float3 RefNormal			 	= tex2D( SKIN,IN.Tex);
		RefNormal				= normalize( RefNormal * 2.0f - 1.0f );
		float2 RefOffset = (RefNormal.xy *(0.1+ 0.1))*0.1;	
		
		float light = saturate( dot (normalize (lightDir), normalize(float3(RefOffset - 0.5f, 0.5f)))) * 0.5f;
		
				
		float3 CameraDir = normalize(IN.Pso - CamPos);
		float3 CubeRefliction = reflect(CameraDir, IN.vNormal+RefNormal);		
		float3 EnivormentMap = texCUBE(envSampler, CubeRefliction);
			
		
		Color.xyz=tex2D(sceneSample,   RUV+RefOffset)+light;
		Color.xyz=Color.xyz+EnivormentMap;
		float DepthDiff=GetDiffAlpha( IN.Depth,IN.P ,IN.ParDepth);	
		Color.a = DepthDiff ;
		return Color;
};

float4 PSWater (	in VS_OUTPUT IN ) : COLOR 
{
		IN.Depth.xz 			= -IN.Depth.xz;
		float2 texelSize	= float2(1.0 / Res.x, 1.0 / Res.y );
		float2 UV     = (0.5 * IN.Depth.xy / IN.Depth.z + 0.5) + ( texelSize / 2.0 );				
		float SceneDepth	= tex2D(DEPTH, UV).w;
		
		WaterNormal			 	= tex2D( TexNormalW, IN.Tex / ( TexScale ) + ( time * WaterSpeed.xy ) ) * 0.50f;
		WaterNormal			 	+= tex2D( TexNormalN, IN.Tex / ( TexScale ) - ( time * WaterSpeed.xy ) ) * 0.50f;
		WaterNormal				= normalize( WaterNormal * 2.0f - 1.0f );

		WaterOffset = (WaterNormal.xy *(0.1+ 0.1)) * 0.1;

		//float light = saturate( dot (normalize (lightDir),WaterNormal )) * 0.05f;
		
		float3 CameraDir = normalize(IN.Pso - CamPos);
		float3 CubeRefliction = reflect(CameraDir, IN.vNormal+WaterNormal);		
		float3 EnivormentMap = texCUBE(envSampler, CubeRefliction)*0.5;
		
		Refraction.r= tex2D(sceneSample,   UV+WaterOffset* 1.15 ).r;
		Refraction.g= tex2D(sceneSample,   UV+WaterOffset ).g;
		Refraction.b= tex2D(sceneSample,   UV+WaterOffset* 0.85  ).b;
		Refraction=Refraction*0.5;
		Refraction=(Refraction+EnivormentMap);
		
		//Color.xyz=Refra*Color.xyz+light;
		Color.xyz=Refraction;
		
		float Dist 				= distance(IN.P,CamPos);		
		float CurDepth		= IN.ParDepth.z / IN.ParDepth.w;
		float DepthDiffuse		= ( SceneDepth - CurDepth ) * Dist * ( Softness * Dist );
		float DepthFoam		= ( SceneDepth - CurDepth ) * Dist * ( 0.1 * Dist );
		DepthDiffuse					= saturate( DepthDiffuse );
		DepthFoam					= saturate( DepthFoam );
		
		float Fog = saturate((Dist - 200) / (500 - 200));
		
		
		Foam = (tex2D(FoamT, ((IN.Tex)/ ( 4 ) + ( time * WaterSpeed.xy ))  + ( WaterNormal.xy / 100 ) ) - DepthFoam*0.9); // * 1.5	
		
		Color.rgb += saturate(Foam);
		Color.rgb=lerp( Color.rgb, float3(0,0,0.02), Fog );
		Color.a    = DepthDiffuse * Alpha ;
		return Color;
};



technique Soft { 
	pass p {	
	    AlphaBlendEnable = true;
  		srcblend = srcalpha;
		destblend = invsrcalpha;
		VertexShader 	= compile vs_2_0 VS();
		PixelShader 	= compile ps_2_0 SoftParticlePS();
	}
}

technique SoftBright { 
	pass p {	
    	AlphaBlendEnable = true;
  	  srcblend = srcalpha;
destblend = One;
		VertexShader 	= compile vs_2_0 VS();
		PixelShader 	= compile ps_2_0 SoftBrightParticlePS();	
	}
}

technique OnlyRender { 
	pass p {	
    	AlphaBlendEnable = true;
  	  srcblend = srcalpha;
destblend = One;
		VertexShader 	= compile vs_2_0 VS();
		PixelShader 	= compile ps_2_0 OnlyRenderPS();	
	}
}

technique Decal { 
	pass p {	
	  AlphaBlendEnable = true;
  srcblend = srcalpha;
destblend = invsrcalpha;
		VertexShader 	= compile vs_2_0 VS();
		PixelShader 	= compile ps_2_0 DecalPS();	
	}
}



technique Refraction { 
	pass p {	
    	AlphaBlendEnable = true;
  	  srcblend = srcalpha;
destblend = invsrcalpha;
		VertexShader 	= compile vs_2_0 VS();
		PixelShader 	= compile ps_2_0 RefractionPS();
	}
}

technique Water { 
	pass p {	
	
	  AlphaBlendEnable = true;
  srcblend = srcalpha;
destblend = invsrcalpha;
CullMode=none;

		VertexShader 	= compile vs_2_0 VS();
		PixelShader 	= compile ps_2_0 PSWater();
		
	}
}


technique Clouds 
{
	pass p0 
	{
		  AlphaBlendEnable = true;
  srcblend = srcalpha;
destblend = invsrcalpha;
CullMode=none;
    
		vertexshader	= compile vs_2_0 VS();
		pixelshader		= compile ps_2_0 psClouds();
	}
}
