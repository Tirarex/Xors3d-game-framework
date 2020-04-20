//   1.00 - upper limit (softer)
//   0.75 - default amount of filtering
//   0.50 - lower limit (sharper, less sub-pixel aliasing removal)
//   0.25 - almost off
//   0.00 - completely off
float fxaaQualitySubpix = 0.25; 

//   0.333 - too little (faster)
//   0.250 - low quality
//   0.166 - default
//   0.125 - high quality 
//   0.063 - overkill (slower)
float fxaaQualityEdgeThreshold = 0.125; 

//   0.0833 - upper limit (default, the start of visible unfiltered edges)
//   0.0625 - high quality (faster)
//   0.0312 - visible limit (slower)
float fxaaQualityEdgeThresholdMin = 0.0625; 

#define FXAA_HLSL_3 1
#define FXAA_QUALITY__PRESET 9

// Includes the Main shader, FXAA 3.11
#include "Deferred/Fxaa.h"

float BUFFER_RCP_WIDTH = 1.0/800;
float BUFFER_RCP_HEIGHT = 1.0/600;

uniform extern texture gScreenTexture ;

sampler screenSampler = sampler_state
{
    Texture = <gScreenTexture>;
    MinFilter = LINEAR;
    MagFilter = LINEAR;
    MipFilter = LINEAR;
    AddressU = BORDER;
    AddressV = BORDER;
    SRGBTexture = FALSE;
};

float4 FXAA( float2 Tex : TEXCOORD0 ) : COLOR0
{
    float4 c0 = FxaaPixelShader(
		// pos, Output color texture
		Tex,
		// tex, Input color texture
		screenSampler,
		// fxaaQualityRcpFrame, gets coordinates for screen width and height, xy
		float2(BUFFER_RCP_WIDTH, BUFFER_RCP_HEIGHT),
		//fxaaConsoleRcpFrameOpt2, gets coordinates for screen width and height, xyzw
		float4(-2.0*BUFFER_RCP_WIDTH,-2.0*BUFFER_RCP_HEIGHT,2.0*BUFFER_RCP_WIDTH,2.0*BUFFER_RCP_HEIGHT),
		// Choose the amount of sub-pixel aliasing removal
		fxaaQualitySubpix,
		// The minimum amount of local contrast required to apply algorithm
		fxaaQualityEdgeThreshold,
		// Trims the algorithm from processing darks
		fxaaQualityEdgeThresholdMin
	);

    return c0;
}


technique PostProcess
{
    pass p1
    {	
        PixelShader = compile ps_3_0 FXAA();
    }
}
