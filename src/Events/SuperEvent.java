package Events;


public sealed interface SuperEvent permits 
EVENT_ToolChanged, 
EVENT_ColorChanged, 
EVENT_RequestBrushSizeSet, 
EVENT_RequestBrushSizeChange, 
EVENT_BrushSizeSet,

EVENT_MousePosition, 
EVENT_ZoomChanged, 
EVENT_ShapeChanged,
EVENT_SetZoom,
EVENT_ChangeZoom,
EVENT_CenterCanvas,
EVENT_UndoCanvas,
EVENT_RedoCanvas,

EVENT_RequestNewLayer, 
EVENT_RequestDeleteLayer, 
EVENT_RequestSelectLayer, 
EVENT_RequestSetLayerVisible, 
EVENT_RequestChangeLayerIndex,

EVENT_NewLayerCreated, 
EVENT_LayerDeleted, 
EVENT_LayerSelected, 
EVENT_LayersReordered, 
EVENT_RequestMergeLayer, 
EVENT_RequestCopyLayer, 

EVENT_DeselectMask, 
EVENT_SelectAll, 
EVENT_CutMask, 
EVENT_CopyMask, 
EVENT_PasteMask

{};
