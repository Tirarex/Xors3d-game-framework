//##################  Varriables  ##################
const float4x4 MatWorldViewProj;

float NoiseStrength = 0.1f;
float RandomValue = 0.0f;
float desa=0.04;

float SpeX;
float SpeY;

float  time	  : CURRENT_TIME;
bool useglitch=0;

const texture tScreen;
sampler TexScreen=sampler_state {
    Texture   = <tScreen>;
    AddressU = Clamp;
    AddressV = Clamp;
    MinFilter = Point;
    MagFilter = Linear;
    MipFilter = Linear;
};

const texture dust;
sampler Texdust=sampler_state {
    Texture   = <dust>;
    AddressU = Clamp;
    AddressV = Clamp;
    MinFilter = Point;
    MagFilter = Linear;
    MipFilter = Linear;
};

const texture Noise;
sampler noiseSample=sampler_state {
    Texture   = <Noise>;
    AddressU = Wrap;
    AddressV = Wrap;
    MinFilter = Point;
    MagFilter = Linear;
    MipFilter = Linear;
};

const texture  tBightles;
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



const texture tBlur;
sampler TexBlur=sampler_state {
    Texture   = <tBlur>;
    AddressU = Clamp;
    AddressV = Clamp;
    MinFilter = Point;
    MagFilter = Linear;
    MipFilter = Linear;
};

const texture tBlur8;
sampler TexBlur8=sampler_state {
    Texture   = <tBlur8>;
    AddressU = Clamp;
    AddressV = Clamp;
    MinFilter = Point;
    MagFilter = Linear;
    MipFilter = Linear;
};

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

struct vis {
	float4 Position : POSITION0;
	float2 TexCoords : TEXCOORD0;
};
struct pis {
	float4 Position : POSITION0;
	float2 TexCoords : TEXCOORD0;
};
void vs( in vis IN, out pis OUT ) {
	OUT.Position		= mul(IN.Position,MatWorldViewProj);
	OUT.TexCoords		= IN.TexCoords;
}

float4 BluF2( in pis IN ) : COLOR
{
   float woffset=0.005;
   float4 color		= tex2D(TexScreen,IN.TexCoords);
   color		+=tex2D(TexScreen,IN.TexCoords+float2(woffset,0))*.55;
   color		+=tex2D(TexScreen,IN.TexCoords+float2(-woffset,0))*.55;
   color		+=tex2D(TexScreen,IN.TexCoords+float2(0,woffset))*.55;
   color		+=tex2D(TexScreen,IN.TexCoords+float2(0,-woffset))*.55;
   color		+=tex2D(TexScreen,IN.TexCoords+float2(woffset,woffset))*.31;
   color		+=tex2D(TexScreen,IN.TexCoords+float2(woffset,-woffset))*.31;
   color		+=tex2D(TexScreen,IN.TexCoords+float2(-woffset,-woffset))*.31;
   color		+=tex2D(TexScreen,IN.TexCoords+float2(-woffset,woffset))*.31;
   color		= color/4.44;
  //color=5*(color-0.45);
   return float4 (color);
}


float2 texel = float2(1.0/800.0,1.0/600.0);
float glowsize = 1.0;

float3 blur(in sampler2D tex, in float2 coords)
{
	float3 col = float3(0,0,0);
	float kernel[25];
	float2 offset[25];
	
	float2 wh = float2(texel.x, texel.y) * glowsize;
	
	offset[0] = float2(-2.0,-2.0)*wh;
	offset[1] = float2(-1.0,-2.0)*wh;
	offset[2] = float2( 0.0,-2.0)*wh;
	offset[3] = float2( 1.0,-2.0)*wh;
	offset[4] = float2( 2.0,-2.0)*wh;

	offset[5] = float2(-2.0,-1.0)*wh;
	offset[6] = float2(-1.0,-1.0)*wh;
	offset[7] = float2( 0.0,-1.0)*wh;
	offset[8] = float2( 1.0,-1.0)*wh;
	offset[9] = float2( 2.0,-1.0)*wh;

	offset[10] = float2(-2.0, 0.0)*wh;
	offset[11] = float2(-1.0, 0.0)*wh;
	offset[12] = float2( 0.0, 0.0)*wh;
	offset[13] = float2( 1.0, 0.0)*wh;
	offset[14] = float2( 2.0, 0.0)*wh;

	offset[15] = float2(-2.0, 1.0)*wh;
	offset[16] = float2(-1.0, 1.0)*wh;
	offset[17] = float2( 0.0, 1.0)*wh;
	offset[18] = float2( 1.0, 1.0)*wh;
	offset[19] = float2( 2.0, 1.0)*wh;

	offset[20] = float2(-2.0, 2.0)*wh;
	offset[21] = float2(-1.0, 2.0)*wh;
	offset[22] = float2( 0.0, 2.0)*wh;
	offset[23] = float2( 1.0, 2.0)*wh;
	offset[24] = float2( 2.0, 2.0)*wh;

	kernel[0] = 1.0/256.0;   kernel[1] = 4.0/256.0;   kernel[2] = 6.0/256.0;   kernel[3] = 4.0/256.0;   kernel[4] = 1.0/256.0;
	kernel[5] = 4.0/256.0;   kernel[6] = 16.0/256.0;  kernel[7] = 24.0/256.0;  kernel[8] = 16.0/256.0;  kernel[9] = 4.0/256.0;
	kernel[10] = 6.0/256.0;  kernel[11] = 24.0/256.0; kernel[12] = 36.0/256.0; kernel[13] = 24.0/256.0; kernel[14] = 6.0/256.0;
	kernel[15] = 4.0/256.0;  kernel[16] = 16.0/256.0; kernel[17] = 24.0/256.0; kernel[18] = 16.0/256.0; kernel[19] = 4.0/256.0;
	kernel[20] = 1.0/256.0;  kernel[21] = 4.0/256.0;  kernel[22] = 6.0/256.0;  kernel[23] = 4.0/256.0;  kernel[24] = 1.0/256.0;

	for( int i=0; i<25; i++ )
	{
		float3 tmp = tex2D(tex, coords + offset[i]).rgb;
		col += tmp * kernel[i];
	}
	
	return col;
}


float4 BluF( in pis IN ) : COLOR
{

   float3 color	= blur(TexScreen,IN.TexCoords).rgb;;
   return float4 (color,0);
}


float threshold = 0.65; //highlight threshold;
float gain = 1.3; //highlight gain;
float FLARE_HALO_WIDTH = 0.65;

float vignette(in float2 coords)
{
	float dist = distance(coords, float2(0.5,0.5));
	dist = smoothstep(FLARE_HALO_WIDTH-0.2, FLARE_HALO_WIDTH, dist);
	return 1-clamp(dist,0.0,1.0);
}
float3 treshold(in sampler2D tex, in float2 coords)
{
	float3 col = tex2D(tex,coords).rgb;

	float3 lumcoeff = float3(0.299,0.587,0.114);
	float lum = dot(col.rgb, lumcoeff);
	float thresh = max((lum-threshold)*gain, 0.0);
	return lerp(float3(0,0,0),col,thresh)*vignette(coords);
}

float3 ACESFilm( float3 x )
{
    float a = 2.51f;
    float b = 0.03f;
    float c = 2.43f;
    float d = 0.59f;
    float e = 0.14f;
    return saturate((x*(a*x+b))/(x*(c*x+d)+e));
}



float4 psHDR( in pis IN ) : COLOR
{


    float3 anamorph ;
    float s;
	for (int i = -1; i < 1; ++i) 
	{
        s = clamp(1.0/abs(float(i)),0.0,1.0);
		anamorph += treshold(TexScreen, float2(IN.TexCoords.x + float(i)*(1.0/64.0),IN.TexCoords.y)).rgb*s;
	}

    float3 dust = tex2D(Texdust,IN.TexCoords).rgb *  vignette(IN.TexCoords);
	//float3 blurX=treshold(TexBlur8, IN.TexCoords);

 	float3 color = float3(0,0,0);
	color.rgb =(anamorph*float3(0.1,0.0,1.0)*2.8)*(dust*0.8+0.4);
    return float4 (color.rgb, 1.0f);
     

}

float3 	DoNightEye(float3 color) {			//Desaturates any color input at night, simulating the rods in the human eye
	
	float amount = 0.8f; 						//How much will the new desaturated and tinted image be mixed with the original image
	float3 rodColor = float3(0.2f, 0.5f, 1.0f); 	//Cyan color that humans percieve when viewing extremely low light levels via rod cells in the eye
	float colorDesat = dot(color, float3(1,1,1)); 	//Desaturated color
	color = lerp(color, colorDesat * rodColor, 0.1 * amount);
	return color ;
	//color.rgb = color.rgb;	
}






 float enhancement=1.2;
 float saturation=1.2;


float4 psHDR2( in pis IN ) : COLOR
{
 	float3 color = float3(0,0,0);
	
float r_dsp = max( (abs(0.5-IN.TexCoords.x)*abs(0.5-IN.TexCoords.y))-0.015 , 0.0);
      color.r=tex2D(TexScreen, (float2(IN.TexCoords.x+.03*r_dsp,IN.TexCoords.y))).r;
      color.g=tex2D(TexScreen, (IN.TexCoords )).g;
      color.b=tex2D(TexScreen, (float2(IN.TexCoords.x-.03*r_dsp,IN.TexCoords.y))).b;
	
	if (useglitch >=1) {
	color.r = color.r*sin(IN.TexCoords.y*100*time)*2;
	color.g = color.g*cos(IN.TexCoords.y*200*time)*2;
	color.b = color.b*sin(IN.TexCoords.y*300*time)*2;
}
	  //color.rgb=color.rgb/(color.rgb+0.4);
	 // color.rgb = lerp(color.rgb,smoothstep(0., 1.,color.rgb),enhancement);
	//  float3 gray = dot(color.rgb, float3(1/3,1/3,1/3));
	//  color.rgb = lerp(gray,color.rgb, saturation);
	// color.rgb = DoNightEye(color.rgb);
	// color.rgb=color.rgb+tex2D(TexBlur,IN.TexCoords );
	  
	  
 // color.rgb=tex2D(TexScreen, (IN.TexCoords )).rgb;
  color.rgb=ACESFilm(color.rgb );

 return float4 (color.rgb, 1.0f);
}




technique Blur {
	pass p0 {

		vertexshader	= compile vs_2_0 vs();
		pixelshader		= compile ps_3_0 BluF2(); 	
	}
}
technique Blur2 {
	pass p0 {

		vertexshader	= compile vs_2_0 vs();
		pixelshader		= compile ps_3_0 BluF(); 	
	}
}




technique HDR {
	pass p0 {
		vertexshader	= compile vs_2_0 vs();
		pixelshader		= compile ps_3_0 psHDR();	
	}
}
technique HDR2 {
	pass p0 {
		vertexshader	= compile vs_2_0 vs();
		pixelshader		= compile ps_3_0 psHDR2();	
	}
}



