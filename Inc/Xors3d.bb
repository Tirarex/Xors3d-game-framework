; *****************************************************************
; *                                                               *
; * Xors3d Engine header file for Blitz3D, (c) 2012 XorsTeam      *
; * www:    http://xors3d.com                                     *
; * e-mail: support@xors3d.com                                    *
; *                                                               *
; *****************************************************************

; Log levels
Const LOG_NO            = 5
Const LOG_FATAL         = 4
Const LOG_ERROR         = 3
Const LOG_WARNING       = 2
Const LOG_MESSAGE       = 1
Const LOG_INFO          = 0

; Log targets
Const LOG_HTML             = 1
Const LOG_COUT             = 2
Const LOG_STRING           = 4

; Skinning types
Const SKIN_SOFTWARE = 2
Const SKIN_HARDWARE = 1

; Light sources types
Const LIGHT_DIRECTIONAL = 1
Const LIGHT_POINT       = 2
Const LIGHT_SPOT        = 3

; Texture filtering
Const TF_NONE           = 0
Const TF_POINT          = 1
Const TF_LINEAR         = 2
Const TF_ANISOTROPIC    = 3
Const TF_ANISOTROPICX4  = 4
Const TF_ANISOTROPICX8  = 5
Const TF_ANISOTROPICX16 = 6

; PixelShader versions
Const PS_1_1 = 0
Const PS_1_2 = 1
Const PS_1_3 = 2
Const PS_1_4 = 3
Const PS_2_0 = 4
Const PS_3_0 = 5

; VertexShader versions
Const VS_1_1 = 0
Const VS_2_0 = 1
Const VS_3_0 = 2

; Matrix semantics
Const WORLD                         = 0
Const WORLDVIEWPROJ                 = 1
Const VIEWPROJ                      = 2
Const VIEW                          = 3
Const PROJ                          = 4
Const WORLDVIEW                     = 5
Const VIEWINVERSE                   = 6
Const WORLDINVERSETRANSPOSE         = 15
Const WORLDINVERSE                  = 16
Const WORLDTRANSPOSE                = 17
Const VIEWPROJINVERSE               = 18
Const VIEWPROJINVERSETRANSPOSE      = 19
Const VIEWTRANSPOSE                 = 20
Const VIEWINVRSETRANSPOSE           = 21
Const PROJINVERSE                   = 22
Const PROJTRANSPOSE                 = 23
Const PROJINVRSETRANSPOSE           = 24
Const WORLDVIEWPROJTRANSPOSE        = 25
Const WORLDVIEWPROJINVERSE          = 26
Const WORLDVIEWPROJINVERSETRANSPOSE = 27
Const WORLDVIEWTRANSPOSE            = 28
Const WORLDVIEWINVERSE              = 29
Const WORLDVIEWINVERSETRANSPOSE     = 30

; Antialiasing types
Const AANONE      = 0
Const AA2SAMPLES  = 1
Const AA3SAMPLES  = 2
Const AA4SAMPLES  = 3
Const AA5SAMPLES  = 4
Const AA6SAMPLES  = 5
Const AA7SAMPLES  = 6
Const AA8SAMPLES  = 7
Const AA9SAMPLES  = 8
Const AA10SAMPLES = 9
Const AA11SAMPLES = 10
Const AA12SAMPLES = 11
Const AA13SAMPLES = 12
Const AA14SAMPLES = 13
Const AA15SAMPLES = 14
Const AA16SAMPLES = 15

; Camera fog mode
Const FOG_NONE     = 0
Const FOG_LINEAR   = 1

; Camera projection mode
Const PROJ_DISABLE      = 0
Const PROJ_PERSPECTIVE	= 1
Const PROJ_ORTHOGRAPHIC = 2

; Entity FX flags
Const FX_NOTHING        = 0
Const FX_FULLBRIGHT     = 1
Const FX_VERTEXCOLOR    = 2
Const FX_FLATSHADED     = 4
Const FX_DISABLEFOG     = 8
Const FX_DISABLECULLING = 16
Const FX_NOALPHABLEND   = 32

; Entity blending modes
Const BLEND_ALPHA       = 1
Const BLEND_MULTIPLY    = 2
Const BLEND_ADD         = 3
Const BLEND_PUREADD     = 4

; Compare functions
Const CMP_NEVER         = 1
Const CMP_LESS          = 2
Const CMP_EQUAL         = 3
Const CMP_LESSEQUAL     = 4
Const CMP_GREATER       = 5
Const CMP_NOTEQUAL      = 6
Const CMP_GREATEREQUAL  = 7
Const CMP_ALWAYS        = 8

; Axis
Const AXIS_X    = 1
Const AXIS_Y    = 2
Const AXIS_Z    = 3

; Texture loading flags
Const FLAGS_COLOR             = 1
Const FLAGS_ALPHA             = 2
Const FLAGS_MASKED            = 4
Const FLAGS_MIPMAPPED         = 8
Const FLAGS_CLAMPU            = 16
Const FLAGS_CLAMPV            = 32
Const FLAGS_SPHERICALENVMAP   = 64
Const FLAGS_CUBICENVMAP       = 128
Const FLAGS_R32F              = 256
Const FLAGS_SKIPCACHE         = 512
Const FLAGS_VOLUMETEXTURE     = 1024
Const FLAGS_ARBG16F           = 2048
Const FLAGS_ARBG32F           = 4096

; Texture blending modes
Const TEXBLEND_NONE          = 0
Const TEXBLEND_ALPHA         = 1
Const TEXBLEND_MULTIPLY      = 2
Const TEXBLEND_ADD           = 3
Const TEXBLEND_DOT3          = 4
Const TEXBLEND_LIGHTMAP      = 5
Const TEXBLEND_SEPARATEALPHA = 6

; Cube map faces
Const FACE_LEFT     = 0
Const FACE_FORWARD  = 1
Const FACE_RIGHT    = 2
Const FACE_BACKWARD = 3
Const FACE_UP       = 4
Const FACE_DOWN     = 5

; Entity animation types
Const ANIMATION_STOP      = 0
Const ANIMATION_LOOP      = 1
Const ANIMATION_PINGPONG  = 2
Const ANIMATION_ONE       = 3

; Collision types
Const SPHERETOSPHERE  = 1
Const SPHERETOBOX     = 3
Const SPHERETOTRIMESH = 2

; Collision respones types
Const RESPONSE_STOP             = 1
Const RESPONSE_SLIDING          = 2
Const RESPONSE_SLIDING_DOWNLOCK = 3

; Entity picking modes
Const PICK_NONE     = 0
Const PICK_SPHERE   = 1
Const PICK_TRIMESH  = 2
Const PICK_BOX      = 3

; Sprite view modes
Const SPRITE_FIXED    = 1
Const SPRITE_FREE     = 2
Const SPRITE_FREEROLL = 3
Const SPRITE_FIXEDYAW = 4

; Joystick types
Const JOY_NONE    = 0
Const JOY_DIGITAL = 1
Const JOY_ANALOG  = 2

; Cubemap rendering modes
Const CUBEMAP_SPECULAR   = 1
Const CUBEMAP_DIFFUSE    = 2
Const CUBEMAP_REFRACTION = 3

; Shadow's blur levels
Const SHADOWS_BLUR_NONE  = 0
Const SHADOWS_BLUR_3     = 1
Const SHADOWS_BLUR_5     = 2
Const SHADOWS_BLUR_7     = 3
Const SHADOWS_BLUR_11    = 4
Const SHADOWS_BLUR_13    = 5

; primitives types
Const PRIMITIVE_POINTLIST     = 1
Const PRIMITIVE_LINELIST      = 2
Const PRIMITIVE_LINESTRIP     = 3
Const PRIMITIVE_TRIANGLELIST  = 4
Const PRIMITIVE_TRIANGLESTRIP = 5
Const PRIMITIVE_TRIANGLEFAN   = 6

; line separator types
Const LS_NUL	= 0
Const LS_CR		= 1
Const LS_LF		= 2
Const LS_CRLF	= 3

; physics: joint types
Const JOINT_POINT2POINT	= 0
Const JOINT_6DOF		= 1
Const JOINT_6DOFSPRING	= 2
Const JOINT_HINGE		= 3

; physics: debug drawer modes
Const PXDD_NO           = 0
Const PXDD_WIREFRAME    = 1
Const PXDD_AABB         = 2
Const PXDD_CONTACTS     = 4
Const PXDD_JOINTS       = 8
Const PXDD_JOINT_LIMITS = 16
Const PXDD_NO_AXIS      = 32

; physics: ray casting modes
Const PXRC_SINGLE   = 0
Const PXRC_MULTIPLE = 1

; 3dlines commands
Function xCreateLine3D%(fromX#, fromY#, fromZ#, toX#, toY#, toZ#, red% = 255, green% = 255, blue% = 255, alpha% = 255, useZBuffer% = True)
	Return xCreateLine3D_%(fromX#, fromY#, fromZ#, toX#, toY#, toZ#, red%, green%, blue%, alpha%, useZBuffer%)
End Function

Function xLine3DOrigin(line3d%, x#, y#, z#, isGlobal% = False)
	xLine3DOrigin_(line3d%, x#, y#, z#, isGlobal%)
End Function

Function xLine3DAddNode(line3d%, x#, y#, z#, isGlobal% = False)
	xLine3DAddNode_(line3d%, x#, y#, z#, isGlobal%)
End Function

Function xLine3DOriginX#(line3d%, isGlobal% = False)
	Return xLine3DOriginX_#(line3d%, isGlobal%)
End Function

Function xLine3DOriginY#(line3d%, isGlobal% = False)
	Return xLine3DOriginY_#(line3d%, isGlobal%)
End Function

Function xLine3DOriginZ#(line3d%, isGlobal% = False)
	Return xLine3DOriginZ_#(line3d%, isGlobal%)
End Function

Function xLine3DNodePosition(line3d%, index%, x#, y#, z#, isGlobal% = False)
	xLine3DNodePosition_(line3d%, index%, x#, y#, z#, isGlobal%)
End Function

Function xLine3DNodeX#(line3d%, index%, isGlobal% = False)
	Return xLine3DNodeX_#(line3d%, index%, isGlobal%)
End Function

Function xLine3DNodeY#(line3d%, index%, isGlobal% = False)
	Return xLine3DNodeY_#(line3d%, index%, isGlobal%)
End Function

Function xLine3DNodeZ#(line3d%, index%, isGlobal% = False)
	Return xLine3DNodeZ_#(line3d%, index%, isGlobal%)
End Function


; brushes commands
Function xLoadBrush%(path$, flags% = 9, xScale# = 1.0, yScale# = 1.0)
	Return xLoadBrush_%(path$, flags%, xScale#, yScale#)
End Function

Function xCreateBrush%(red# = 255.0, green# = 255.0, blue# = 255.0)
	Return xCreateBrush_%(red#, green#, blue#)
End Function

Function xGetBrushTexture%(brush%, index% = 0)
	Return xGetBrushTexture_%(brush%, index%)
End Function

Function xBrushTexture(brush%, texture%, frame% = 0, index% = 0)
	xBrushTexture_(brush%, texture%, frame%, index%)
End Function


; cameras commands
Function xCameraClsColor(camera%, red%, green%, blue%, alpha% = 255)
	xCameraClsColor_(camera%, red%, green%, blue%, alpha%)
End Function

Function xCreateCamera%(parent% = 0)
	Return xCreateCamera_%(parent%)
End Function


; collisions commands
Function xEntityRadius(entity%, xRadius#, yRadius# = 0.0)
	xEntityRadius_(entity%, xRadius#, yRadius#)
End Function

Function xEntityType(entity%, typeID%, recurse% = False)
	xEntityType_(entity%, typeID%, recurse%)
End Function


; constants commands

; effects commands
Function xSetEntityEffect(entity%, effect%, index% = -1)
	xSetEntityEffect_(entity%, effect%, index%)
End Function

Function xSetSurfaceEffect(surface%, effect%, index% = -1)
	xSetSurfaceEffect_(surface%, effect%, index%)
End Function

Function xSetBonesArrayName(entity%, arrayName$, layer% = -1)
	xSetBonesArrayName_(entity%, arrayName$, layer%)
End Function

Function xSurfaceBonesArrayName(surface%, arrayName$, layer% = -1)
	xSurfaceBonesArrayName_(surface%, arrayName$, layer%)
End Function

Function xSetEffectInt(entity%, name$, value%, layer% = -1)
	xSetEffectInt_(entity%, name$, value%, layer%)
End Function

Function xSurfaceEffectInt(surface%, name$, value%, layer% = -1)
	xSurfaceEffectInt_(surface%, name$, value%, layer%)
End Function

Function xSetEffectFloat(entity%, name$, value#, layer% = -1)
	xSetEffectFloat_(entity%, name$, value#, layer%)
End Function

Function xSurfaceEffectFloat(surface%, name$, value#, layer% = -1)
	xSurfaceEffectFloat_(surface%, name$, value#, layer%)
End Function

Function xSetEffectBool(entity%, name$, value%, layer% = -1)
	xSetEffectBool_(entity%, name$, value%, layer%)
End Function

Function xSurfaceEffectBool(surface%, name$, value%, layer% = -1)
	xSurfaceEffectBool_(surface%, name$, value%, layer%)
End Function

Function xSetEffectVector(entity%, name$, x#, y#, z#, w# = 0.0, layer% = -1)
	xSetEffectVector_(entity%, name$, x#, y#, z#, w#, layer%)
End Function

Function xSurfaceEffectVector(surface%, name$, x#, y#, z#, w# = 0.0, layer% = -1)
	xSurfaceEffectVector_(surface%, name$, x#, y#, z#, w#, layer%)
End Function

Function xSetEffectVectorArray(entity%, name$, value%, count%, layer% = -1)
	xSetEffectVectorArray_(entity%, name$, value%, count%, layer%)
End Function

Function xSurfaceEffectVectorArray(surface%, name$, value%, count%, layer% = -1)
	xSurfaceEffectVectorArray_(surface%, name$, value%, count%, layer%)
End Function

Function xSurfaceEffectMatrixArray(surface%, name$, value%, count%, layer% = -1)
	xSurfaceEffectMatrixArray_(surface%, name$, value%, count%, layer%)
End Function

Function xSurfaceEffectFloatArray(surface%, name$, value%, count%, layer% = -1)
	xSurfaceEffectFloatArray_(surface%, name$, value%, count%, layer%)
End Function

Function xSurfaceEffectIntArray(surface%, name$, value%, count%, layer% = -1)
	xSurfaceEffectIntArray_(surface%, name$, value%, count%, layer%)
End Function

Function xSetEffectMatrixArray(entity%, name$, value%, count%, layer% = -1)
	xSetEffectMatrixArray_(entity%, name$, value%, count%, layer%)
End Function

Function xSetEffectFloatArray(entity%, name$, value%, count%, layer% = -1)
	xSetEffectFloatArray_(entity%, name$, value%, count%, layer%)
End Function

Function xSetEffectIntArray(entity%, name$, value%, count%, layer% = -1)
	xSetEffectIntArray_(entity%, name$, value%, count%, layer%)
End Function

Function xSetEffectMatrixWithElements(entity%, name$, m11#, m12#, m13#, m14#, m21#, m22#, m23#, m24#, m31#, m32#, m33#, m34#, m41#, m42#, m43#, m44#, layer% = -1)
	xSetEffectMatrixWithElements_(entity%, name$, m11#, m12#, m13#, m14#, m21#, m22#, m23#, m24#, m31#, m32#, m33#, m34#, m41#, m42#, m43#, m44#, layer%)
End Function

Function xSetEffectMatrix(entity%, name$, matrix%, layer% = -1)
	xSetEffectMatrix_(entity%, name$, matrix%, layer%)
End Function

Function xSurfaceEffectMatrix(surface%, name$, matrix%, layer% = -1)
	xSurfaceEffectMatrix_(surface%, name$, matrix%, layer%)
End Function

Function xSurfaceEffectMatrixWithElements(surface%, name$, m11#, m12#, m13#, m14#, m21#, m22#, m23#, m24#, m31#, m32#, m33#, m34#, m41#, m42#, m43#, m44#, layer% = -1)
	xSurfaceEffectMatrixWithElements_(surface%, name$, m11#, m12#, m13#, m14#, m21#, m22#, m23#, m24#, m31#, m32#, m33#, m34#, m41#, m42#, m43#, m44#, layer%)
End Function

Function xSetEffectEntityTexture(entity%, name$, index% = 0, layer% = -1)
	xSetEffectEntityTexture_(entity%, name$, index%, layer%)
End Function

Function xSetEffectTexture(entity%, name$, texture%, frame% = 0, layer% = -1, isRecursive% = 1)
	xSetEffectTexture_(entity%, name$, texture%, frame%, layer%, isRecursive%)
End Function

Function xSurfaceEffectTexture(surface%, name$, texture%, frame% = 0, layer% = -1)
	xSurfaceEffectTexture_(surface%, name$, texture%, frame%, layer%)
End Function

Function xSurfaceEffectMatrixSemantic(surface%, name$, value%, layer% = -1)
	xSurfaceEffectMatrixSemantic_(surface%, name$, value%, layer%)
End Function

Function xSetEffectMatrixSemantic(entity%, name$, value%, layer% = -1)
	xSetEffectMatrixSemantic_(entity%, name$, value%, layer%)
End Function

Function xDeleteSurfaceConstant(surface%, name$, layer% = -1)
	xDeleteSurfaceConstant_(surface%, name$, layer%)
End Function

Function xDeleteEffectConstant(entity%, name$, layer% = -1)
	xDeleteEffectConstant_(entity%, name$, layer%)
End Function

Function xClearSurfaceConstants(surface%, layer% = -1)
	xClearSurfaceConstants_(surface%, layer%)
End Function

Function xClearEffectConstants(entity%, layer% = -1)
	xClearEffectConstants_(entity%, layer%)
End Function

Function xSetEffectTechnique(entity%, name$, layer% = -1)
	xSetEffectTechnique_(entity%, name$, layer%)
End Function

Function xSurfaceTechnique(surface%, name$, layer% = -1)
	xSurfaceTechnique_(surface%, name$, layer%)
End Function

Function xSetFXVector(effect%, name$, x#, y#, z#, w# = 0.0)
	xSetFXVector_(effect%, name$, x#, y#, z#, w#)
End Function

Function xSetFXTexture(effect%, name$, texture%, frame% = 0)
	xSetFXTexture_(effect%, name$, texture%, frame%)
End Function


; emitters commands
Function xCreateEmitter%(psystem%, parent% = 0)
	Return xCreateEmitter_%(psystem%, parent%)
End Function


; entity_animation commands
Function xExtractAnimSeq%(entity%, firstFrame%, lastFrame%, sequence% = 0)
	Return xExtractAnimSeq_%(entity%, firstFrame%, lastFrame%, sequence%)
End Function

Function xSetAnimSpeed(entity%, speed#, rootBone$ = "")
	xSetAnimSpeed_(entity%, speed#, rootBone$)
End Function

Function xAnimSpeed#(entity%, rootBone$ = "")
	Return xAnimSpeed_#(entity%, rootBone$)
End Function

Function xAnimating%(entity%, rootBone$ = "")
	Return xAnimating_%(entity%, rootBone$)
End Function

Function xAnimTime#(entity%, rootBone$ = "")
	Return xAnimTime_#(entity%, rootBone$)
End Function

Function xAnimate(entity%, mode% = 1, speed# = 1.0, sequence% = 0, translate# = 0.0, rootBone$ = "")
	xAnimate_(entity%, mode%, speed#, sequence%, translate#, rootBone$)
End Function

Function xAnimSeq%(entity%, rootBone$ = "")
	Return xAnimSeq_%(entity%, rootBone$)
End Function

Function xAnimLength#(entity%, rootBone$ = "")
	Return xAnimLength_#(entity%, rootBone$)
End Function

Function xSetAnimTime(entity%, time#, sequence%, rootBone$ = "")
	xSetAnimTime_(entity%, time#, sequence%, rootBone$)
End Function

Function xSetAnimFrame(entity%, frame#, sequence%, rootBone$ = "")
	xSetAnimFrame_(entity%, frame#, sequence%, rootBone$)
End Function


; entity_control commands
Function xCopyEntity%(entity%, parent% = 0, cloneBuffers% = 0)
	Return xCopyEntity_%(entity%, parent%, cloneBuffers%)
End Function

Function xEntityPickMode(entity%, mode%, obscurer% = True, recursive% = True)
	xEntityPickMode_(entity%, mode%, obscurer%, recursive%)
End Function

Function xEntityTexture(entity%, texture%, frame% = 0, index% = 0, isRecursive% = 1)
	xEntityTexture_(entity%, texture%, frame%, index%, isRecursive%)
End Function

Function xEntityParent(entity%, parent% = 0, isGlobal% = True)
	xEntityParent_(entity%, parent%, isGlobal%)
End Function

Function xCreateInstance%(entity%, parent% = 0)
	Return xCreateInstance_%(entity%, parent%)
End Function

Function xFreezeInstances(entity%, enable% = True)
	xFreezeInstances_(entity%, enable%)
End Function


; entity_movement commands
Function xScaleEntity(entity%, x#, y#, z#, isGlobal% = False)
	xScaleEntity_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xPositionEntity(entity%, x#, y#, z#, isGlobal% = False)
	xPositionEntity_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xMoveEntity(entity%, x#, y#, z#, isGlobal% = False)
	xMoveEntity_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xTranslateEntity(entity%, x#, y#, z#, isGlobal% = False)
	xTranslateEntity_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xRotateEntity(entity%, x#, y#, z#, isGlobal% = False)
	xRotateEntity_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xTurnEntity(entity%, x#, y#, z#, isGlobal% = False)
	xTurnEntity_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xPointEntity(entity1%, entity2%, roll# = 0.0)
	xPointEntity_(entity1%, entity2%, roll#)
End Function

Function xAlignToVector(entity%, x#, y#, z#, axis%, factor# = 1.0)
	xAlignToVector_(entity%, x#, y#, z#, axis%, factor#)
End Function


; entity_state commands
Function xEntityX#(entity%, isGlobal% = False)
	Return xEntityX_#(entity%, isGlobal%)
End Function

Function xEntityY#(entity%, isGlobal% = False)
	Return xEntityY_#(entity%, isGlobal%)
End Function

Function xEntityZ#(entity%, isGlobal% = False)
	Return xEntityZ_#(entity%, isGlobal%)
End Function

Function xEntityRoll#(entity%, isGlobal% = False)
	Return xEntityRoll_#(entity%, isGlobal%)
End Function

Function xEntityYaw#(entity%, isGlobal% = False)
	Return xEntityYaw_#(entity%, isGlobal%)
End Function

Function xEntityPitch#(entity%, isGlobal% = False)
	Return xEntityPitch_#(entity%, isGlobal%)
End Function


; filesystems commands
Function xMountPackFile%(path$, mountpoint$ = "", password$ = "")
	Return xMountPackFile_%(path$, mountpoint$, password$)
End Function

Function xReadLine$(file%, ls_flag% = 0)
	Return xReadLine_$(file%, ls_flag%)
End Function

Function xWriteLine(file%, value$, ls_flag% = 0)
	xWriteLine_(file%, value$, ls_flag%)
End Function


; fonts commands
Function xLoadFont%(name$, height%, bold% = False, italic% = False, underline% = False, fontface$ = "")
	Return xLoadFont_%(name$, height%, bold%, italic%, underline%, fontface$)
End Function

Function xText(x#, y#, textString$, centerx% = False, centery% = False)
	xText_(x#, y#, textString$, centerx%, centery%)
End Function


; graphics commands
Function xRect(x%, y%, width%, height%, solid% = False)
	xRect_(x%, y%, width%, height%, solid%)
End Function

Function xOval(x%, y%, width%, height%, solid% = False)
	xOval_(x%, y%, width%, height%, solid%)
End Function

Function xLockBuffer(buffer% = 0)
	xLockBuffer_(buffer%)
End Function

Function xUnlockBuffer(buffer% = 0)
	xUnlockBuffer_(buffer%)
End Function

Function xWritePixelFast(x%, y%, argb%, buffer% = -1)
	xWritePixelFast_(x%, y%, argb%, buffer%)
End Function

Function xReadPixelFast%(x%, y%, buffer% = -1)
	Return xReadPixelFast_%(x%, y%, buffer%)
End Function

Function xGetPixels%(buffer% = -1)
	Return xGetPixels_%(buffer%)
End Function

Function xBufferWidth%(buffer% = 0)
	Return xBufferWidth_%(buffer%)
End Function

Function xBufferHeight%(buffer% = 0)
	Return xBufferHeight_%(buffer%)
End Function

Function xSetBuffer(buffer% = 0)
	xSetBuffer_(buffer%)
End Function

Function xTextureBuffer%(texture%, frame% = 0)
	Return xTextureBuffer_%(texture%, frame%)
End Function

Function xWritePixel(x%, y%, argb%, buffer% = 0)
	xWritePixel_(x%, y%, argb%, buffer%)
End Function

Function xReadPixel%(x%, y%, buffer% = 0)
	Return xReadPixel_%(x%, y%, buffer%)
End Function

Function xGraphicsWidth%(isVirtual% = True)
	Return xGraphicsWidth_%(isVirtual%)
End Function

Function xGraphicsHeight%(isVirtual% = True)
	Return xGraphicsHeight_%(isVirtual%)
End Function

Function xClsColor(red%, green%, blue%, alpha% = 255)
	xClsColor_(red%, green%, blue%, alpha%)
End Function

Function xClearWorld(entities% = True, brushes% = True, textures% = True)
	xClearWorld_(entities%, brushes%, textures%)
End Function

Function xColor(red%, green%, blue%, alpha% = 255)
	xColor_(red%, green%, blue%, alpha%)
End Function

Function xUpdateWorld(speed# = 1.0)
	xUpdateWorld_(speed#)
End Function

Function xRenderEntity(camera%, entity%, tween# = 1.0)
	xRenderEntity_(camera%, entity%, tween#)
End Function

Function xRenderWorld(tween# = 1.0, renderShadows% = False)
	xRenderWorld_(tween#, renderShadows%)
End Function

Function xAmbientLight(red%, green%, blue%, world% = 0)
	xAmbientLight_(red%, green%, blue%, world%)
End Function

Function xGraphics3D(width% = 1024, height% = 768, depth% = 0, mode% = 0, vsync% = 1)
	xSetWND(SystemProperty("AppHWND"))
	xGraphics3D_(width%, height%, depth%, mode%, vsync%)
End Function

Function xDrawMovementGizmo(x#, y#, z#, selectMask% = 0)
	xDrawMovementGizmo_(x#, y#, z#, selectMask%)
End Function

Function xDrawScaleGizmo(x#, y#, z#, selectMask% = 0, sx# = 1.0, sy# = 1.0, sz# = 1.0)
	xDrawScaleGizmo_(x#, y#, z#, selectMask%, sx#, sy#, sz#)
End Function

Function xDrawRotationGizmo(x#, y#, z#, selectMask% = 0, pitch# = 0.0, yaw# = 0.0, roll# = 0.0)
	xDrawRotationGizmo_(x#, y#, z#, selectMask%, pitch#, yaw#, roll#)
End Function

Function xDeltaTime%(fromInit% = False)
	Return xDeltaTime_%(fromInit%)
End Function

Function xDeltaValue#(value#, time% = 0)
	Return xDeltaValue_#(value#, time%)
End Function


; images commands
Function xImageBuffer%(image%, frame% = 0)
	Return xImageBuffer_%(image%, frame%)
End Function

Function xCreateImage%(width%, height%, frame% = 1)
	Return xCreateImage_%(width%, height%, frame%)
End Function

Function xGrabImage(image%, x%, y%, frame% = 0)
	xGrabImage_(image%, x%, y%, frame%)
End Function

Function xSaveImage(image%, path$, frame% = 0)
	xSaveImage_(image%, path$, frame%)
End Function

Function xDrawImage(image%, x#, y#, frame% = 0)
	xDrawImage_(image%, x#, y#, frame%)
End Function

Function xDrawImageRect(image%, x#, y#, rectx#, recty#, rectWidth#, rectHeight#, frame% = 0)
	xDrawImageRect_(image%, x#, y#, rectx#, recty#, rectWidth#, rectHeight#, frame%)
End Function

Function xTileImage(image%, x#, y#, frame% = 0)
	xTileImage_(image%, x#, y#, frame%)
End Function

Function xDrawBlock(image%, x#, y#, frame% = 0)
	xDrawBlock_(image%, x#, y#, frame%)
End Function

Function xDrawBlockRect(image%, x#, y#, rectx#, recty#, rectWidth#, rectHeight#, frame% = 0)
	xDrawBlockRect_(image%, x#, y#, rectx#, recty#, rectWidth#, rectHeight#, frame%)
End Function


; inputs commands

; joysticks commands
Function xJoyType%(portID% = 0)
	Return xJoyType_%(portID%)
End Function

Function xJoyDown%(key%, portID% = 0)
	Return xJoyDown_%(key%, portID%)
End Function

Function xJoyHit%(key%, portID% = 0)
	Return xJoyHit_%(key%, portID%)
End Function

Function xGetJoy%(portID% = 0)
	Return xGetJoy_%(portID%)
End Function

Function xWaitJoy%(portID% = 0)
	Return xWaitJoy_%(portID%)
End Function

Function xJoyX#(portID% = 0)
	Return xJoyX_#(portID%)
End Function

Function xJoyY#(portID% = 0)
	Return xJoyY_#(portID%)
End Function

Function xJoyZ#(portID% = 0)
	Return xJoyZ_#(portID%)
End Function

Function xJoyU#(portID% = 0)
	Return xJoyU_#(portID%)
End Function

Function xJoyV#(portID% = 0)
	Return xJoyV_#(portID%)
End Function

Function xJoyPitch#(portID% = 0)
	Return xJoyPitch_#(portID%)
End Function

Function xJoyYaw#(portID% = 0)
	Return xJoyYaw_#(portID%)
End Function

Function xJoyRoll#(portID% = 0)
	Return xJoyRoll_#(portID%)
End Function

Function xJoyHat#(portID% = 0)
	Return xJoyHat_#(portID%)
End Function

Function xJoyXDir%(portID% = 0)
	Return xJoyXDir_%(portID%)
End Function

Function xJoyYDir%(portID% = 0)
	Return xJoyYDir_%(portID%)
End Function

Function xJoyZDir%(portID% = 0)
	Return xJoyZDir_%(portID%)
End Function

Function xJoyUDir%(portID% = 0)
	Return xJoyUDir_%(portID%)
End Function

Function xJoyVDir%(portID% = 0)
	Return xJoyVDir_%(portID%)
End Function


; lights commands
Function xCreateLight%(typeID% = 1)
	Return xCreateLight_%(typeID%)
End Function


; logging commands
Function xCreateLog%(target% = 1, level% = 0, filename$ = "xors_log.html", cssfilename$ = "")
	Return xCreateLog_%(target%, level%, filename$, cssfilename$)
End Function

Function xSetLogLevel(level% = 2)
	xSetLogLevel_(level%)
End Function

Function xSetLogTarget(target% = 1)
	xSetLogTarget_(target%)
End Function

Function xLogInfo(message$, func$ = "", file$ = "", line% = -1)
	xLogInfo_(message$, func$, file$, Line%)
End Function

Function xLogMessage(message$, func$ = "", file$ = "", line% = -1)
	xLogMessage_(message$, func$, file$, Line%)
End Function

Function xLogWarning(message$, func$ = "", file$ = "", line% = -1)
	xLogWarning_(message$, func$, file$, Line%)
End Function

Function xLogError(message$, func$ = "", file$ = "", line% = -1)
	xLogError_(message$, func$, file$, Line%)
End Function

Function xLogFatal(message$, func$ = "", file$ = "", line% = -1)
	xLogFatal_(message$, func$, file$, Line%)
End Function


; meshes commands
Function xCreateMesh%(parent% = 0)
	Return xCreateMesh_%(parent%)
End Function

Function xLoadMesh%(path$, parent% = 0)
	Return xLoadMesh_%(path$, parent%)
End Function

Function xLoadMeshWithChild%(path$, parent% = 0)
	Return xLoadMeshWithChild_%(path$, parent%)
End Function

Function xLoadAnimMesh%(path$, parent% = 0)
	Return xLoadAnimMesh_%(path$, parent%)
End Function

Function xCreateCube%(parent% = 0)
	Return xCreateCube_%(parent%)
End Function

Function xCreateSphere%(segments% = 16, parent% = 0)
	Return xCreateSphere_%(segments%, parent%)
End Function

Function xCreateCylinder%(segments% = 16, solid% = True, parent% = 0)
	Return xCreateCylinder_%(segments%, solid%, parent%)
End Function

Function xCreateTorus%(segments% = 16, R# = 1.0, r_tube# = 0.025, parent% = 0)
	Return xCreateTorus_%(segments%, R#, r_tube#, parent%)
End Function

Function xCreateCone%(segments% = 16, solid% = True, parent% = 0)
	Return xCreateCone_%(segments%, solid%, parent%)
End Function

Function xCopyMesh%(entity%, parent% = 0)
	Return xCopyMesh_%(entity%, parent%)
End Function

Function xFitMesh(entity%, x#, y#, z#, width#, height#, depth#, uniform% = False)
	xFitMesh_(entity%, x#, y#, z#, width#, height#, depth#, uniform%)
End Function

Function xMeshWidth#(entity%, recursive% = False)
	Return xMeshWidth_#(entity%, recursive%)
End Function

Function xMeshHeight#(entity%, recursive% = False)
	Return xMeshHeight_#(entity%, recursive%)
End Function

Function xMeshDepth#(entity%, recursive% = False)
	Return xMeshDepth_#(entity%, recursive%)
End Function

Function xCreatePivot%(parent% = 0)
	Return xCreatePivot_%(parent%)
End Function

Function xCreatePoly%(sides% = 0, parent% = 0)
	Return xCreatePoly_%(sides%, parent%)
End Function

Function xLightMesh(entity%, red%, green%, blue%, range# = 0.0, lightX# = 0.0, lightY# = 0.0, lightZ# = 0.0)
	xLightMesh_(entity%, red%, green%, blue%, range#, lightX#, lightY#, lightZ#)
End Function


; particles commands

; physics commands
Function xEntityAddBoxShape(entity%, mass#, width# = 0.0, height# = 0.0, depth# = 0.0)
	xEntityAddBoxShape_(entity%, mass#, width#, height#, depth#)
End Function

Function xEntityAddSphereShape(entity%, mass#, radius# = 0.0)
	xEntityAddSphereShape_(entity%, mass#, radius#)
End Function

Function xEntityAddCapsuleShape(entity%, mass#, radius# = 0.0, height# = 0.0)
	xEntityAddCapsuleShape_(entity%, mass#, radius#, height#)
End Function

Function xEntityAddConeShape(entity%, mass#, radius# = 0.0, height# = 0.0)
	xEntityAddConeShape_(entity%, mass#, radius#, height#)
End Function

Function xEntityAddCylinderShape(entity%, mass#, width# = 0.0, height# = 0.0, depth# = 0.0)
	xEntityAddCylinderShape_(entity%, mass#, width#, height#, depth#)
End Function

Function xCreateHingeJoint%(firstBody%, secondBody%, pivotX#, pivotY#, pivotZ#, axisX#, axisY#, axisZ#, isGlobal% = False)
	Return xCreateHingeJoint_%(firstBody%, secondBody%, pivotX#, pivotY#, pivotZ#, axisX#, axisY#, axisZ#, isGlobal%)
End Function

Function xCreateBallJoint%(firstBody%, secondBody%, pivotX#, pivotY#, pivotZ#, isGlobal% = False)
	Return xCreateBallJoint_%(firstBody%, secondBody%, pivotX#, pivotY#, pivotZ#, isGlobal%)
End Function

Function xCreateD6Joint%(firstBody%, secondBody%, pivot1X#, pivot1Y#, pivot1Z#, pivot2X#, pivot2Y#, pivot2Z#, isGlobal1% = False, isGlobal2% = False)
	Return xCreateD6Joint_%(firstBody%, secondBody%, pivot1X#, pivot1Y#, pivot1Z#, pivot2X#, pivot2Y#, pivot2Z#, isGlobal1%, isGlobal2%)
End Function

Function xCreateD6SpringJoint%(firstBody%, secondBody%, pivot1X#, pivot1Y#, pivot1Z#, pivot2X#, pivot2Y#, pivot2Z#, isGlobal1% = False, isGlobal2% = False)
	Return xCreateD6SpringJoint_%(firstBody%, secondBody%, pivot1X#, pivot1Y#, pivot1Z#, pivot2X#, pivot2Y#, pivot2Z#, isGlobal1%, isGlobal2%)
End Function

Function xJointD6GetAngle#(joint%, axis% = 0)
	Return xJointD6GetAngle_#(joint%, axis%)
End Function

Function xJointBallSetPivot(joint%, x#, y#, z#, isGlobal% = False)
	xJointBallSetPivot_(joint%, x#, y#, z#, isGlobal%)
End Function

Function xJointBallGetPivotX#(joint%, isGlobal% = False)
	Return xJointBallGetPivotX_#(joint%, isGlobal%)
End Function

Function xJointBallGetPivotY#(joint%, isGlobal% = False)
	Return xJointBallGetPivotY_#(joint%, isGlobal%)
End Function

Function xJointBallGetPivotZ#(joint%, isGlobal% = False)
	Return xJointBallGetPivotZ_#(joint%, isGlobal%)
End Function

Function xJointD6SpringSetParam(joint%, index%, enabled%, damping# = 1.0, stiffness# = 1.0)
	xJointD6SpringSetParam_(joint%, index%, enabled%, damping#, stiffness#)
End Function

Function xJointHingeSetLimits(joint%, lowerLimit#, upperLimit#, softness# = 0.9, biasFactor# = 0.3, relaxationFactor# = 1.0)
	xJointHingeSetLimits_(joint%, lowerLimit#, upperLimit#, softness#, biasFactor#, relaxationFactor#)
End Function

Function xJointEnableMotor(joint%, enabled%, targetVelocity#, maxForce#, index% = 0)
	xJointEnableMotor_(joint%, enabled%, targetVelocity#, maxForce#, index%)
End Function

Function xEntityApplyCentralForce(entity%, x#, y#, z#, isGlobal% = True)
	xEntityApplyCentralForce_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xEntityApplyCentralImpulse(entity%, x#, y#, z#, isGlobal% = True)
	xEntityApplyCentralImpulse_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xEntityApplyTorque(entity%, x#, y#, z#, isGlobal% = True)
	xEntityApplyTorque_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xEntityApplyTorqueImpulse(entity%, x#, y#, z#, isGlobal% = True)
	xEntityApplyTorqueImpulse_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xEntityApplyForce(entity%, x#, y#, z#, pointx#, pointy#, pointz#, isGlobal% = True, globalPoint% = True)
	xEntityApplyForce_(entity%, x#, y#, z#, pointx#, pointy#, pointz#, isGlobal%, globalPoint%)
End Function

Function xEntityApplyImpulse(entity%, x#, y#, z#, pointx#, pointy#, pointz#, isGlobal% = True, globalPoint% = True)
	xEntityApplyImpulse_(entity%, x#, y#, z#, pointx#, pointy#, pointz#, isGlobal%, globalPoint%)
End Function

Function xWorldSetGravity(x#, y#, z#, world% = 0)
	xWorldSetGravity_(x#, y#, z#, world%)
End Function

Function xWorldGetGravityX#(world% = 0)
	Return xWorldGetGravityX_#(world%)
End Function

Function xWorldGetGravityY#(world% = 0)
	Return xWorldGetGravityY_#(world%)
End Function

Function xWorldGetGravityZ#(world% = 0)
	Return xWorldGetGravityZ_#(world%)
End Function

Function xEntitySetLinearVelocity(entity%, x#, y#, z#, isGlobal% = True)
	xEntitySetLinearVelocity_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xEntityGetLinearVelocityX#(entity%, isGlobal% = True)
	Return xEntityGetLinearVelocityX_#(entity%, isGlobal%)
End Function

Function xEntityGetLinearVelocityY#(entity%, isGlobal% = True)
	Return xEntityGetLinearVelocityY_#(entity%, isGlobal%)
End Function

Function xEntityGetLinearVelocityZ#(entity%, isGlobal% = True)
	Return xEntityGetLinearVelocityZ_#(entity%, isGlobal%)
End Function

Function xEntitySetAngularVelocity(entity%, x#, y#, z#, isGlobal% = True)
	xEntitySetAngularVelocity_(entity%, x#, y#, z#, isGlobal%)
End Function

Function xEntityGetAngularVelocityX#(entity%, isGlobal% = True)
	Return xEntityGetAngularVelocityX_#(entity%, isGlobal%)
End Function

Function xEntityGetAngularVelocityY#(entity%, isGlobal% = True)
	Return xEntityGetAngularVelocityY_#(entity%, isGlobal%)
End Function

Function xEntityGetAngularVelocityZ#(entity%, isGlobal% = True)
	Return xEntityGetAngularVelocityZ_#(entity%, isGlobal%)
End Function

Function xEntityDisableSleeping(entity%, state% = 1)
	xEntityDisableSleeping_(entity%, state%)
End Function

Function xPhysicsRayCast(fromX#, fromY#, fromZ#, toX#, toY#, toZ#, rcType% = 0, rayGroup% = 0)
	xPhysicsRayCast_(fromX#, fromY#, fromZ#, toX#, toY#, toZ#, rcType%, rayGroup%)
End Function

Function xPhysicsGetHitEntity%(index% = 0)
	Return xPhysicsGetHitEntity_%(index%)
End Function

Function xPhysicsGetHitPointX#(index% = 0)
	Return xPhysicsGetHitPointX_#(index%)
End Function

Function xPhysicsGetHitPointY#(index% = 0)
	Return xPhysicsGetHitPointY_#(index%)
End Function

Function xPhysicsGetHitPointZ#(index% = 0)
	Return xPhysicsGetHitPointZ_#(index%)
End Function

Function xPhysicsGetHitNormalX#(index% = 0)
	Return xPhysicsGetHitNormalX_#(index%)
End Function

Function xPhysicsGetHitNormalY#(index% = 0)
	Return xPhysicsGetHitNormalY_#(index%)
End Function

Function xPhysicsGetHitNormalZ#(index% = 0)
	Return xPhysicsGetHitNormalZ_#(index%)
End Function

Function xPhysicsGetHitDistance#(index% = 0)
	Return xPhysicsGetHitDistance_#(index%)
End Function

Function xWorldSetFrequency(frequency#, world% = 0)
	xWorldSetFrequency_(frequency#, world%)
End Function

Function xEntityWheelSetConnectionPoint(chassisEntity%, index%, x#, y#, z#, isGlobal% = False)
	xEntityWheelSetConnectionPoint_(chassisEntity%, index%, x#, y#, z#, isGlobal%)
End Function


; posteffects commands
Function xSetPostEffect(index%, postEffect%, technique$ = "MainTechnique")
	xSetPostEffect_(index%, postEffect%, technique$)
End Function

Function xSetPostEffectVector(postEffect%, name$, x#, y#, z#, w# = 1.0)
	xSetPostEffectVector_(postEffect%, name$, x#, y#, z#, w#)
End Function

Function xSetPostEffectTexture(postEffect%, name$, texture%, frame% = 0)
	xSetPostEffectTexture_(postEffect%, name$, texture%, frame%)
End Function


; psystems commands
Function xCreatePSystem%(pointSprites% = False)
	Return xCreatePSystem_%(pointSprites%)
End Function


; raypicks commands
Function xLinePick%(x#, y#, z#, dx#, dy#, dz#, distance# = 0.0)
	Return xLinePick_%(x#, y#, z#, dx#, dy#, dz#, distance#)
End Function

Function xEntityPick%(entity%, range# = 0.0)
	Return xEntityPick_%(entity%, range#)
End Function


; shadows commands
Function xSetShadowParams(splitPlanes% = 4, splitLambda# = 0.95, useOrtho% = True, lightDist# = 300.0)
	xSetShadowParams_(splitPlanes%, splitLambda#, useOrtho%, lightDist#)
End Function


; sounds commands
Function xCreateListener%(parent% = 0, roFactor# = 1.0, doplerFactor# = 1.0, distFactor# = 1.0)
	Return xCreateListener_%(parent%, roFactor#, doplerFactor#, distFactor#)
End Function


; sprites commands
Function xCreateSprite%(parent% = 0)
	Return xCreateSprite_%(parent%)
End Function

Function xLoadSprite%(path$, flags% = 9, parent% = 0)
	Return xLoadSprite_%(path$, flags%, parent%)
End Function


; surfaces commands
Function xCreateSurface%(entity%, brush% = 0, dynamic% = False)
	Return xCreateSurface_%(entity%, brush%, dynamic%)
End Function

Function xAddVertex%(surface%, x#, y#, z#, u# = 0.0, v# = 0.0, w# = 0.0)
	Return xAddVertex_%(surface%, x#, y#, z#, u#, v#, w#)
End Function

Function xVertexColor(surface%, vertex%, red%, green%, blue%, alpha# = 1.0)
	xVertexColor_(surface%, vertex%, red%, green%, blue%, alpha#)
End Function

Function xVertexTexCoords(surface%, vertex%, u#, v#, w# = 1.0, textureSet% = 0)
	xVertexTexCoords_(surface%, vertex%, u#, v#, w#, textureSet%)
End Function

Function xVertexU#(surface%, vertex%, textureSet% = 0)
	Return xVertexU_#(surface%, vertex%, textureSet%)
End Function

Function xVertexV#(surface%, vertex%, textureSet% = 0)
	Return xVertexV_#(surface%, vertex%, textureSet%)
End Function

Function xVertexW#(surface%, vertex%, textureSet% = 0)
	Return xVertexW_#(surface%, vertex%, textureSet%)
End Function

Function xClearSurface(surface%, vertices% = True, triangles% = True)
	xClearSurface_(surface%, vertices%, triangles%)
End Function

Function xGetSurfaceTexture%(surface%, index% = 0)
	Return xGetSurfaceTexture_%(surface%, index%)
End Function


; sysinfos commands

; terrains commands
Function xLoadTerrain%(path$, parent% = 0)
	Return xLoadTerrain_%(path$, parent%)
End Function

Function xCreateTerrain%(size%, parent% = 0)
	Return xCreateTerrain_%(size%, parent%)
End Function

Function xTerrainShading(terrain%, state% = False)
	xTerrainShading_(terrain%, state%)
End Function

Function xModifyTerrain(terrain%, x%, y%, height#, realtime% = False)
	xModifyTerrain_(terrain%, x%, y%, height#, realtime%)
End Function

Function xTerrainViewZone(terrain%, viewZone%, texturingZone% = -1)
	xTerrainViewZone_(terrain%, viewZone%, texturingZone%)
End Function


; textures commands
Function xCreateTexture%(width%, height%, flags% = 9, frames% = 1)
	Return xCreateTexture_%(width%, height%, flags%, frames%)
End Function

Function xLoadTexture%(path$, flags% = 9)
	Return xLoadTexture_%(path$, flags%)
End Function

Function xCreateTextureFromData%(pixelsData%, width%, height%, flags% = 9, frames% = 1)
	Return xCreateTextureFromData_%(pixelsData%, width%, height%, flags%, frames%)
End Function

Function xGetTextureData%(texture%, frame% = 0)
	Return xGetTextureData_%(texture%, frame%)
End Function

Function xGetTextureDataPitch%(texture%, frame% = 0)
	Return xGetTextureDataPitch_%(texture%, frame%)
End Function

Function xGetTextureSurface%(texture%, frame% = 0)
	Return xGetTextureSurface_%(texture%, frame%)
End Function


; transforms commands

; videos commands
Function xDrawMovie(video%, x% = 0, y% = 0, width% = -1, height% = -1)
	xDrawMovie_(video%, x%, y%, width%, height%)
End Function

Function xMovieSeek(video%, time#, relative% = False)
	xMovieSeek_(video%, time#, relative%)
End Function


; worlds commands
; Scancodes for keyboard and mouse
Const MOUSE_LEFT         = 1
Const MOUSE_RIGHT        = 2
Const MOUSE_MIDDLE       = 3
Const MOUSE4             = 4
Const MOUSE5             = 5
Const MOUSE6             = 6
Const MOUSE7             = 7
Const MOUSE8             = 8

Const xMOUSE_LEFT        = 1
Const xMOUSE_RIGHT       = 2
Const xMOUSE_MIDDLE      = 3
Const xMOUSE4            = 4
Const xMOUSE5            = 5
Const xMOUSE6            = 6
Const xMOUSE7            = 7
Const xMOUSE8            = 8

Const KEY_ESCAPE         = 1
Const KEY_1              = 2
Const KEY_2              = 3
Const KEY_3              = 4
Const KEY_4              = 5
Const KEY_5              = 6
Const KEY_6              = 7
Const KEY_7              = 8
Const KEY_8              = 9
Const KEY_9              = 10
Const KEY_0              = 11
Const KEY_MINUS          = 12
Const KEY_EQUALS         = 13
Const KEY_BACK           = 14
Const KEY_TAB            = 15
Const KEY_Q              = 16
Const KEY_W              = 17
Const KEY_E              = 18
Const KEY_R              = 19
Const KEY_T              = 20
Const KEY_Y              = 21
Const KEY_U              = 22
Const KEY_I              = 23
Const KEY_O              = 24
Const KEY_P              = 25
Const KEY_LBRACKET       = 26
Const KEY_RBRACKET       = 27
Const KEY_RETURN         = 28
Const KEY_ENTER          = KEY_RETURN
Const KEY_LCONTROL       = 29
Const KEY_RCONTROL       = 157
Const KEY_A              = 30
Const KEY_S              = 31
Const KEY_D              = 32
Const KEY_F              = 33
Const KEY_G              = 34
Const KEY_H              = 35
Const KEY_J              = 36
Const KEY_K              = 37
Const KEY_L              = 38
Const KEY_SEMICOLON      = 39
Const KEY_APOSTROPHE     = 40
Const KEY_GRAVE          = 41
Const KEY_LSHIFT         = 42
Const KEY_BACKSLASH      = 43
Const KEY_Z              = 44
Const KEY_X              = 45
Const KEY_C              = 46
Const KEY_V              = 47
Const KEY_B              = 48
Const KEY_N              = 49
Const KEY_M              = 50
Const KEY_COMMA          = 51
Const KEY_PERIOD         = 52
Const KEY_SLASH          = 53
Const KEY_RSHIFT         = 54
Const KEY_MULTIPLY       = 55
Const KEY_MENU           = 56
Const KEY_SPACE          = 57
Const KEY_F1             = 59
Const KEY_F2             = 60
Const KEY_F3             = 61
Const KEY_F4             = 62
Const KEY_F5             = 63
Const KEY_F6             = 64
Const KEY_F7             = 65
Const KEY_F8             = 66
Const KEY_F9             = 67
Const KEY_F10            = 68
Const KEY_NUMLOCK        = 69
Const KEY_SCROLL         = 70
Const KEY_NUMPAD7        = 71
Const KEY_NUMPAD8        = 72
Const KEY_NUMPAD9        = 73
Const KEY_SUBTRACT       = 74
Const KEY_NUMPAD4        = 75
Const KEY_NUMPAD5        = 76
Const KEY_NUMPAD6        = 77
Const KEY_ADD            = 78
Const KEY_NUMPAD1        = 79
Const KEY_NUMPAD2        = 80
Const KEY_NUMPAD3        = 81
Const KEY_NUMPAD0        = 82
Const KEY_DECIMAL        = 83
Const KEY_TILD           = 86
Const KEY_F11            = 87
Const KEY_F12            = 88
Const KEY_NUMPADENTER    = 156
Const KEY_RMENU          = 221
Const KEY_PAUSE          = 197
Const KEY_HOME           = 199
Const KEY_UP             = 200
Const KEY_PRIOR          = 201
Const KEY_LEFT           = 203
Const KEY_RIGHT          = 205
Const KEY_END            = 207
Const KEY_DOWN           = 208
Const KEY_NEXT           = 209
Const KEY_INSERT         = 210
Const KEY_DELETE         = 211
Const KEY_LWIN           = 219
Const KEY_RWIN           = 220
Const KEY_BACKSPACE      = KEY_BACK
Const KEY_NUMPADSTAR     = KEY_MULTIPLY
Const KEY_CAPSLOCK       = 58
Const KEY_NUMPADMINUS    = KEY_SUBTRACT
Const KEY_NUMPADPLUS     = KEY_ADD
Const KEY_NUMPADPERIOD   = KEY_DECIMAL
Const KEY_DIVIDE         = 181
Const KEY_NUMPADSLASH    = KEY_DIVIDE
Const KEY_LALT           = 56
Const KEY_RALT           = 184
Const KEY_UPARROW        = KEY_UP
Const KEY_PGUP           = KEY_PRIOR
Const KEY_LEFTARROW      = KEY_LEFT
Const KEY_RIGHTARROW     = KEY_RIGHT
Const KEY_DOWNARROW      = KEY_DOWN
Const KEY_PGDN           = KEY_NEXT

Const xKEY_ESCAPE        = 1
Const xKEY_1             = 2
Const xKEY_2             = 3
Const xKEY_3             = 4
Const xKEY_4             = 5
Const xKEY_5             = 6
Const xKEY_6             = 7
Const xKEY_7             = 8
Const xKEY_8             = 9
Const xKEY_9             = 10
Const xKEY_0             = 11
Const xKEY_MINUS         = 12
Const xKEY_EQUALS        = 13
Const xKEY_BACK          = 14
Const xKEY_TAB           = 15
Const xKEY_Q             = 16
Const xKEY_W             = 17
Const xKEY_E             = 18
Const xKEY_R             = 19
Const xKEY_T             = 20
Const xKEY_Y             = 21
Const xKEY_U             = 22
Const xKEY_I             = 23
Const xKEY_O             = 24
Const xKEY_P             = 25
Const xKEY_LBRACKET      = 26
Const xKEY_RBRACKET      = 27
Const xKEY_RETURN        = 28
Const xKEY_ENTER         = KEY_RETURN
Const xKEY_LCONTROL      = 29
Const xKEY_RCONTROL      = 157
Const xKEY_A             = 30
Const xKEY_S             = 31
Const xKEY_D             = 32
Const xKEY_F             = 33
Const xKEY_G             = 34
Const xKEY_H             = 35
Const xKEY_J             = 36
Const xKEY_K             = 37
Const xKEY_L             = 38
Const xKEY_SEMICOLON     = 39
Const xKEY_APOSTROPHE    = 40
Const xKEY_GRAVE         = 41
Const xKEY_LSHIFT        = 42
Const xKEY_BACKSLASH     = 43
Const xKEY_Z             = 44
Const xKEY_X             = 45
Const xKEY_C             = 46
Const xKEY_V             = 47
Const xKEY_B             = 48
Const xKEY_N             = 49
Const xKEY_M             = 50
Const xKEY_COMMA         = 51
Const xKEY_PERIOD        = 52
Const xKEY_SLASH         = 53
Const xKEY_RSHIFT        = 54
Const xKEY_MULTIPLY      = 55
Const xKEY_MENU          = 56
Const xKEY_SPACE         = 57
Const xKEY_F1            = 59
Const xKEY_F2            = 60
Const xKEY_F3            = 61
Const xKEY_F4            = 62
Const xKEY_F5            = 63
Const xKEY_F6            = 64
Const xKEY_F7            = 65
Const xKEY_F8            = 66
Const xKEY_F9            = 67
Const xKEY_F10           = 68
Const xKEY_NUMLOCK       = 69
Const xKEY_SCROLL        = 70
Const xKEY_NUMPAD7       = 71
Const xKEY_NUMPAD8       = 72
Const xKEY_NUMPAD9       = 73
Const xKEY_SUBTRACT      = 74
Const xKEY_NUMPAD4       = 75
Const xKEY_NUMPAD5       = 76
Const xKEY_NUMPAD6       = 77
Const xKEY_ADD           = 78
Const xKEY_NUMPAD1       = 79
Const xKEY_NUMPAD2       = 80
Const xKEY_NUMPAD3       = 81
Const xKEY_NUMPAD0       = 82
Const xKEY_DECIMAL       = 83
Const xKEY_TILD          = 86
Const xKEY_F11           = 87
Const xKEY_F12           = 88
Const xKEY_NUMPADENTER   = 156
Const xKEY_RMENU         = 221
Const xKEY_PAUSE         = 197
Const xKEY_HOME          = 199
Const xKEY_UP            = 200
Const xKEY_PRIOR         = 201
Const xKEY_LEFT          = 203
Const xKEY_RIGHT         = 205
Const xKEY_END           = 207
Const xKEY_DOWN          = 208
Const xKEY_NEXT          = 209
Const xKEY_INSERT        = 210
Const xKEY_DELETE        = 211
Const xKEY_LWIN          = 219
Const xKEY_RWIN          = 220
Const xKEY_BACKSPACE     = KEY_BACK
Const xKEY_NUMPADSTAR    = KEY_MULTIPLY
Const xKEY_CAPSLOCK      = 58
Const xKEY_NUMPADMINUS   = KEY_SUBTRACT
Const xKEY_NUMPADPLUS    = KEY_ADD
Const xKEY_NUMPADPERIOD  = KEY_DECIMAL
Const xKEY_DIVIDE        = 181
Const xKEY_NUMPADSLASH   = KEY_DIVIDE
Const xKEY_LALT          = 56
Const xKEY_RALT          = 184
Const xKEY_UPARROW       = KEY_UP
Const xKEY_PGUP          = KEY_PRIOR
Const xKEY_LEFTARROW     = KEY_LEFT
Const xKEY_RIGHTARROW    = KEY_RIGHT
Const xKEY_DOWNARROW     = KEY_DOWN
Const xKEY_PGDN          = KEY_NEXT
;~IDEal Editor Parameters:
;~C#Blitz3D